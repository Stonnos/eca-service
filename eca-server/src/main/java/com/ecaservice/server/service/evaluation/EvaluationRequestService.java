package com.ecaservice.server.service.evaluation;

import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.s3.client.minio.exception.ObjectStorageException;
import com.ecaservice.s3.client.minio.model.GetPresignedUrlObject;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.bpm.model.EvaluationStatus;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.config.ClassifiersProperties;
import com.ecaservice.server.exception.EvaluationTimeoutException;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.model.evaluation.EvaluationInputDataModel;
import com.ecaservice.server.model.evaluation.EvaluationRequestData;
import com.ecaservice.server.model.evaluation.EvaluationResultsDataModel;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.TaskWorker;
import com.ecaservice.server.service.data.InstancesLoaderService;
import com.ecaservice.server.service.evaluation.initializers.ClassifierInitializerService;
import eca.core.evaluation.EvaluationResults;
import eca.core.model.ClassificationModel;
import io.micrometer.tracing.annotation.NewSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.ecaservice.server.util.ClassifierOptionsHelper.parseOptions;
import static com.ecaservice.server.util.ClassifierOptionsHelper.toJsonString;
import static com.ecaservice.server.util.Utils.buildClassificationModel;
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
    private final ExecutorService executorService;
    private final EvaluationService evaluationService;
    private final ClassifierInitializerService classifierInitializerService;
    private final ObjectStorageService objectStorageService;
    private final InstancesLoaderService instancesLoaderService;
    private final EvaluationLogService evaluationLogService;
    private final ClassifierOptionsAdapter classifierOptionsAdapter;
    private final EvaluationLogRepository evaluationLogRepository;

    /**
     * Creates evaluation request.
     *
     * @param evaluationRequestData - evaluation request data model
     * @return evaluation log entity
     */
    public EvaluationLog createAndSaveEvaluationRequest(EvaluationRequestData evaluationRequestData) {
        log.info("Starting to create evaluation request [{}] for classifier [{}]",
                evaluationRequestData.getRequestId(),
                evaluationRequestData.getClassifier().getClass().getSimpleName());
        var evaluationLog = evaluationLogService.createAndSaveEvaluationLog(evaluationRequestData);
        log.info("Evaluation request [{}] has been created for classifier [{}]",
                evaluationRequestData.getRequestId(),
                evaluationRequestData.getClassifier().getClass().getSimpleName());
        return evaluationLog;
    }

    /**
     * Starts classifier evaluation request.
     *
     * @param evaluationLog - evaluation log
     */
    public void startEvaluationRequest(EvaluationLog evaluationLog) {
        log.info("Starts evaluation request [{}] for classifier [{}]", evaluationLog.getRequestId(),
                evaluationLog.getClassifierName());
        evaluationLogService.startEvaluationLog(evaluationLog);
        log.info("Evaluation request [{}] has been started for classifier [{}]", evaluationLog.getRequestId(),
                evaluationLog.getClassifierName());
    }

    /**
     * Processes classifier evaluation.
     *
     * @param evaluationLog - evaluation log
     * @return evaluation results data model
     */
    @NewSpan
    public EvaluationResultsDataModel processEvaluationRequest(EvaluationLog evaluationLog) {
        log.info("Starting to process request for classifier [{}] evaluation with data uuid [{}]",
                evaluationLog.getClassifierName(), evaluationLog.getTrainingDataUuid());
        try {
            Instances data = instancesLoaderService.loadInstances(evaluationLog.getTrainingDataUuid());
            //Initialize classifier options based on training data
            AbstractClassifier classifier = initializeClassifier(data, evaluationLog);
            //Save updated classifier options
            updateClassifierOptions(classifier, evaluationLog);
            var evaluationResultsDataModel = internalProcessRequest(classifier, data, evaluationLog);
            evaluationLog.setEvaluationTimeMillis(
                    evaluationResultsDataModel.getEvaluationResults().getEvaluation().getTotalTimeMillis());
            evaluationLogService.finishEvaluation(evaluationLog, RequestStatus.FINISHED);
            return evaluationResultsDataModel;
        } catch (ObjectStorageException ex) {
            log.error("S3 object storage error while process evaluation request [{}]: {}",
                    evaluationLog.getRequestId(), ex.getMessage());
            evaluationLog.setRetryAt(LocalDateTime.now().plusSeconds(classifiersProperties.getRetryIntervalSeconds()));
            evaluationLogRepository.save(evaluationLog);
            return buildEvaluationResultsModel(evaluationLog.getRequestId(), evaluationLog.getRequestStatus(),
                    EvaluationStatus.FAILED);
        } catch (EvaluationTimeoutException ex) {
            log.error("There was a timeout for evaluation [{}].", evaluationLog.getRequestId());
            evaluationLogService.finishEvaluation(evaluationLog, RequestStatus.TIMEOUT);
            return buildEvaluationResultsModel(evaluationLog.getRequestId(), RequestStatus.TIMEOUT,
                    EvaluationStatus.TIMEOUT);
        } catch (Exception ex) {
            log.error("There was an error occurred for evaluation [{}]: {}", evaluationLog.getRequestId(),
                    ex.getMessage());
            evaluationLogService.finishEvaluation(evaluationLog, RequestStatus.ERROR);
            return buildInternalErrorEvaluationResultsModel(evaluationLog.getRequestId());
        }
    }

    private AbstractClassifier initializeClassifier(Instances data, EvaluationLog evaluationLog) {
        var initialClassifierOptions = parseOptions(evaluationLog.getClassifierOptions());
        var classifier = classifierOptionsAdapter.convert(initialClassifierOptions);
        classifierInitializerService.initialize(classifier, data);
        return classifier;
    }

    private void updateClassifierOptions(AbstractClassifier classifier, EvaluationLog evaluationLog) {
        var finalClassifierOptions = classifierOptionsAdapter.convert(classifier);
        evaluationLog.setClassifierOptions(toJsonString(finalClassifierOptions));
        evaluationLogRepository.save(evaluationLog);
    }

    private EvaluationResultsDataModel internalProcessRequest(AbstractClassifier classifier,
                                                              Instances data,
                                                              EvaluationLog evaluationLog)
            throws IOException, ExecutionException, InterruptedException {
        var evaluationResults = evaluationModel(classifier, data, evaluationLog);
        uploadModel(evaluationResults, evaluationLog);
        String modelUrl = getModelPresignedUrl(evaluationLog.getModelPath());
        processEvaluationResults(evaluationResults, evaluationLog);
        EvaluationResultsDataModel evaluationResultsDataModel =
                buildEvaluationResultsModel(evaluationLog.getRequestId(), RequestStatus.FINISHED,
                        EvaluationStatus.SUCCESS);
        evaluationResultsDataModel.setEvaluationResults(evaluationResults);
        evaluationResultsDataModel.setModelUrl(modelUrl);
        log.info("Evaluation request [{}] has been successfully finished.", evaluationLog.getRequestId());
        return evaluationResultsDataModel;
    }

    private EvaluationResults evaluationModel(AbstractClassifier classifier,
                                              Instances data,
                                              EvaluationLog evaluationLog)
            throws ExecutionException, InterruptedException {
        var evaluationInputDataModel = new EvaluationInputDataModel();
        evaluationInputDataModel.setClassifier(classifier);
        evaluationInputDataModel.setData(data);
        evaluationInputDataModel.setEvaluationMethod(evaluationLog.getEvaluationMethod());
        evaluationInputDataModel.setNumFolds(evaluationLog.getNumFolds());
        evaluationInputDataModel.setNumTests(evaluationLog.getNumTests());
        evaluationInputDataModel.setSeed(evaluationLog.getSeed());
        TaskWorker<EvaluationResults> taskWorker = new TaskWorker<>(executorService);
        try {
            Callable<EvaluationResults> task = () -> evaluationService.evaluateModel(evaluationInputDataModel);
            return taskWorker.performTask(task, classifiersProperties.getEvaluationTimeoutMinutes(), TimeUnit.MINUTES);
        } catch (TimeoutException ex) {
            taskWorker.cancel();
            log.warn("Evaluation [{}] has been cancelled by timeout", evaluationLog.getRequestId());
            throw new EvaluationTimeoutException(ex.getMessage());
        }
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

    private void processEvaluationResults(EvaluationResults evaluationResults, EvaluationLog evaluationLog) {
        double pctCorrect = evaluationResults.getEvaluation().pctCorrect();
        evaluationLog.setPctCorrect(BigDecimal.valueOf(pctCorrect));
    }
}
