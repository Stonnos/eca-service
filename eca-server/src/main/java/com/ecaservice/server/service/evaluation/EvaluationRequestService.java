package com.ecaservice.server.service.evaluation;

import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.s3.client.minio.model.GetPresignedUrlObject;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.config.ClassifiersProperties;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.model.evaluation.ClassificationResult;
import com.ecaservice.server.model.evaluation.EvaluationInputDataModel;
import com.ecaservice.server.model.evaluation.EvaluationRequestDataModel;
import com.ecaservice.server.model.evaluation.EvaluationResultsDataModel;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.InstancesInfoService;
import com.ecaservice.server.service.data.InstancesLoaderService;
import com.ecaservice.server.service.data.InstancesMetaDataService;
import com.ecaservice.server.service.evaluation.initializers.ClassifierInitializerService;
import eca.core.evaluation.EvaluationResults;
import eca.core.model.ClassificationModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.ecaservice.common.web.util.LogHelper.EV_REQUEST_ID;
import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.common.web.util.LogHelper.putMdcIfAbsent;
import static com.ecaservice.server.util.ClassifierOptionsHelper.toJsonString;
import static com.ecaservice.server.util.Utils.buildEvaluationResultsModel;
import static com.ecaservice.server.util.Utils.buildInternalErrorEvaluationResultsModel;

/**
 * Evaluation request service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationRequestService {

    private static final String CLASSIFIER_PATH_FORMAT = "classifier-%s.model";

    private final AppProperties appProperties;
    private final ClassifiersProperties classifiersProperties;
    private final CrossValidationConfig crossValidationConfig;
    private final CalculationExecutorService executorService;
    private final EvaluationService evaluationService;
    private final EvaluationLogRepository evaluationLogRepository;
    private final EvaluationLogMapper evaluationLogMapper;
    private final ClassifierInitializerService classifierInitializerService;
    private final ClassifierOptionsAdapter classifierOptionsAdapter;
    private final ObjectStorageService objectStorageService;
    private final InstancesInfoService instancesInfoService;
    private final InstancesMetaDataService instancesMetaDataService;
    private final InstancesLoaderService instancesLoaderService;

    /**
     * Processes input request and returns classification results.
     *
     * @param evaluationRequestDataModel - evaluation request data model
     * @return evaluation response data model
     */
    public EvaluationResultsDataModel processRequest(final EvaluationRequestDataModel evaluationRequestDataModel) {
        String requestId = UUID.randomUUID().toString();
        putMdcIfAbsent(TX_ID, requestId);
        putMdc(EV_REQUEST_ID, requestId);
        log.info("Starting to process request for classifier [{}] evaluation with data uuid [{}]",
                evaluationRequestDataModel.getClassifier().getClass().getSimpleName(),
                evaluationRequestDataModel.getDataUuid());
        Instances data = instancesLoaderService.loadInstances(evaluationRequestDataModel.getDataUuid());
        classifierInitializerService.initialize(evaluationRequestDataModel.getClassifier(), data);
        EvaluationLog evaluationLog = createAndSaveEvaluationLog(requestId, evaluationRequestDataModel);
        try {
            return internalProcessRequest(evaluationRequestDataModel, evaluationLog, data);
        } catch (TimeoutException ex) {
            log.warn("There was a timeout for evaluation [{}].", evaluationLog.getRequestId());
            handleTimeout(evaluationLog);
            return buildEvaluationResultsModel(requestId, RequestStatus.TIMEOUT);
        } catch (Exception ex) {
            log.error("There was an error occurred for evaluation [{}]: {}", evaluationLog.getRequestId(), ex);
            handleError(evaluationLog, ex.getMessage());
            return buildInternalErrorEvaluationResultsModel(requestId);
        }
    }

    private EvaluationResultsDataModel internalProcessRequest(EvaluationRequestDataModel evaluationRequestDataModel,
                                                              EvaluationLog evaluationLog,
                                                              Instances data)
            throws IOException, ExecutionException, InterruptedException, TimeoutException {
        ClassificationResult classificationResult = evaluationModel(evaluationRequestDataModel, data);
        if (classificationResult.isSuccess()) {
            handleSuccessModel(classificationResult, evaluationLog);
            EvaluationResultsDataModel evaluationResultsDataModel =
                    buildEvaluationResultsModel(evaluationLog.getRequestId(), RequestStatus.FINISHED);
            evaluationResultsDataModel.setEvaluationResults(classificationResult.getEvaluationResults());
            String modelUrl = getModelPresignedUrl(evaluationLog.getModelPath());
            evaluationResultsDataModel.setModelUrl(modelUrl);
            log.info("Evaluation request [{}] has been successfully finished.",
                    evaluationResultsDataModel.getRequestId());
            return evaluationResultsDataModel;
        } else {
            handleError(evaluationLog, classificationResult.getErrorMessage());
            return buildInternalErrorEvaluationResultsModel(evaluationLog.getRequestId());
        }
    }

    private ClassificationResult evaluationModel(EvaluationRequestDataModel evaluationRequestDataModel, Instances data)
            throws ExecutionException, InterruptedException, TimeoutException {
        var evaluationInputDataModel = new EvaluationInputDataModel();
        evaluationInputDataModel.setClassifier(evaluationRequestDataModel.getClassifier());
        evaluationInputDataModel.setData(data);
        evaluationInputDataModel.setEvaluationMethod(evaluationRequestDataModel.getEvaluationMethod());
        evaluationInputDataModel.setNumFolds(evaluationRequestDataModel.getNumFolds());
        evaluationInputDataModel.setNumTests(evaluationRequestDataModel.getNumTests());
        evaluationInputDataModel.setSeed(evaluationRequestDataModel.getSeed());
        Callable<ClassificationResult> callable = () -> evaluationService.evaluateModel(evaluationInputDataModel);
        return executorService.execute(callable, classifiersProperties.getTimeout(), TimeUnit.MINUTES);
    }

    private EvaluationLog createAndSaveEvaluationLog(String requestId,
                                                     EvaluationRequestDataModel evaluationRequestDataModel) {
        var instancesMetaDataModel =
                instancesMetaDataService.getInstancesMetaData(evaluationRequestDataModel.getDataUuid());
        var instancesInfo = instancesInfoService.getOrSaveInstancesInfo(instancesMetaDataModel);
        EvaluationLog evaluationLog = evaluationLogMapper.map(evaluationRequestDataModel, crossValidationConfig);
        evaluationLog.setInstancesInfo(instancesInfo);
        processClassifierOptions(evaluationRequestDataModel.getClassifier(), evaluationLog);
        evaluationLog.setRequestStatus(RequestStatus.IN_PROGRESS);
        evaluationLog.setRequestId(requestId);
        evaluationLog.setCreationDate(LocalDateTime.now());
        evaluationLog.setStartDate(LocalDateTime.now());
        return evaluationLogRepository.save(evaluationLog);
    }

    private void processClassifierOptions(AbstractClassifier classifier, EvaluationLog evaluationLog) {
        var classifierOptions = classifierOptionsAdapter.convert(classifier);
        evaluationLog.getClassifierInfo().setClassifierOptions(toJsonString(classifierOptions));
    }

    private ClassificationModel buildClassificationModel(EvaluationResults evaluationResults) {
        var classifier = (AbstractClassifier) evaluationResults.getClassifier();
        ClassificationModel classificationModel = new ClassificationModel();
        classificationModel.setClassifier(classifier);
        classificationModel.setData(evaluationResults.getEvaluation().getData());
        classificationModel.setEvaluation(evaluationResults.getEvaluation());
        return classificationModel;
    }

    private void uploadModel(EvaluationResults evaluationResults, EvaluationLog evaluationLog) throws IOException {
        String modelPath = String.format(CLASSIFIER_PATH_FORMAT, evaluationLog.getRequestId());
        ClassificationModel classificationModel = buildClassificationModel(evaluationResults);
        objectStorageService.uploadObject(classificationModel, modelPath);
        evaluationLog.setModelPath(modelPath);
    }

    private String getModelPresignedUrl(String modelPath) {
        return objectStorageService.getObjectPresignedProxyUrl(
                GetPresignedUrlObject.builder()
                        .objectPath(modelPath)
                        .expirationTime(appProperties.getModelDownloadUrlExpirationDays())
                        .expirationTimeUnit(TimeUnit.DAYS)
                        .build()
        );
    }

    private void handleSuccessModel(ClassificationResult classificationResult,
                                    EvaluationLog evaluationLog) throws IOException {
        uploadModel(classificationResult.getEvaluationResults(), evaluationLog);
        evaluationLog.setRequestStatus(RequestStatus.FINISHED);
        double pctCorrect = classificationResult.getEvaluationResults().getEvaluation().pctCorrect();
        evaluationLog.setPctCorrect(BigDecimal.valueOf(pctCorrect));
        evaluationLog.setEndDate(LocalDateTime.now());
        evaluationLogRepository.save(evaluationLog);
    }

    private void handleError(EvaluationLog evaluationLog, String errorMessage) {
        evaluationLog.setRequestStatus(RequestStatus.ERROR);
        evaluationLog.setErrorMessage(errorMessage);
        evaluationLog.setEndDate(LocalDateTime.now());
        evaluationLogRepository.save(evaluationLog);
    }

    private void handleTimeout(EvaluationLog evaluationLog) {
        evaluationLog.setRequestStatus(RequestStatus.TIMEOUT);
        evaluationLog.setEndDate(LocalDateTime.now());
        evaluationLogRepository.save(evaluationLog);
    }
}
