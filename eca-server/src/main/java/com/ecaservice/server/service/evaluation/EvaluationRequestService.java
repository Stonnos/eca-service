package com.ecaservice.server.service.evaluation;

import com.ecaservice.base.model.ErrorCode;
import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.MessageError;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.s3.client.minio.model.GetPresignedUrlObject;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.model.evaluation.ClassificationResult;
import com.ecaservice.server.model.evaluation.EvaluationRequestDataModel;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.evaluation.initializers.ClassifierInitializerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.classifiers.AbstractClassifier;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.ecaservice.common.web.util.LogHelper.EV_REQUEST_ID;
import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.common.web.util.LogHelper.putMdcIfAbsent;
import static com.ecaservice.server.util.ClassifierOptionsHelper.toJsonString;
import static com.ecaservice.server.util.Utils.error;

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

    private final CrossValidationConfig crossValidationConfig;
    private final CalculationExecutorService executorService;
    private final EvaluationService evaluationService;
    private final EvaluationLogRepository evaluationLogRepository;
    private final EvaluationLogMapper evaluationLogMapper;
    private final ClassifierInitializerService classifierInitializerService;
    private final ClassifierOptionsAdapter classifierOptionsAdapter;
    private final ObjectStorageService objectStorageService;

    /**
     * Processes input request and returns classification results.
     *
     * @param evaluationRequestDataModel - evaluation request data model
     * @return evaluation response
     */
    public EvaluationResponse processRequest(final EvaluationRequestDataModel evaluationRequestDataModel) {
        String requestId = UUID.randomUUID().toString();
        putMdcIfAbsent(TX_ID, requestId);
        putMdc(EV_REQUEST_ID, requestId);
        log.info("Received request for classifier [{}] evaluation with data [{}]",
                evaluationRequestDataModel.getClassifier().getClass().getSimpleName(),
                evaluationRequestDataModel.getData().relationName());
        classifierInitializerService.initialize(evaluationRequestDataModel.getClassifier(),
                evaluationRequestDataModel.getData());
        EvaluationLog evaluationLog = createAndSaveEvaluationLog(requestId, evaluationRequestDataModel);
        EvaluationResponse evaluationResponse = new EvaluationResponse();
        evaluationResponse.setRequestId(evaluationLog.getRequestId());
        try {
            Callable<ClassificationResult> callable = () -> evaluationService.evaluateModel(evaluationRequestDataModel);
            ClassificationResult classificationResult = executorService.execute(callable,
                    crossValidationConfig.getTimeout(), TimeUnit.MINUTES);
            if (classificationResult.isSuccess()) {
                handleSuccessModel(classificationResult, evaluationLog, evaluationResponse);
            } else {
                handleError(evaluationLog, evaluationResponse, classificationResult.getErrorMessage());
            }
        } catch (TimeoutException ex) {
            log.warn("There was a timeout for evaluation [{}].", evaluationLog.getRequestId());
            evaluationLog.setRequestStatus(RequestStatus.TIMEOUT);
            evaluationResponse.setStatus(TechnicalStatus.TIMEOUT);
        } catch (Exception ex) {
            log.error("There was an error occurred for evaluation [{}]: {}", evaluationLog.getRequestId(), ex);
            handleError(evaluationLog, evaluationResponse, ex.getMessage());
        } finally {
            evaluationLog.setEndDate(LocalDateTime.now());
            evaluationLogRepository.save(evaluationLog);
        }
        return evaluationResponse;
    }

    private EvaluationLog createAndSaveEvaluationLog(String requestId,
                                                     EvaluationRequestDataModel evaluationRequestDataModel) {
        EvaluationLog evaluationLog = evaluationLogMapper.map(evaluationRequestDataModel, crossValidationConfig);
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

    private void uploadModel(AbstractClassifier classifier, EvaluationLog evaluationLog) throws IOException {
        objectStorageService.uploadObject(classifier,
                String.format(CLASSIFIER_PATH_FORMAT, evaluationLog.getRequestId()));
    }

    private String getModelPresignedUrl(EvaluationLog evaluationLog) {
        return objectStorageService.getObjectPresignedProxyUrl(
                GetPresignedUrlObject.builder()
                        .objectPath(String.format("classifier-%s.model", evaluationLog.getRequestId()))
                        .expirationTime(3)
                        .expirationTimeUnit(TimeUnit.DAYS)
                        .build()
        );
    }

    private void handleSuccessModel(ClassificationResult classificationResult,
                                    EvaluationLog evaluationLog,
                                    EvaluationResponse evaluationResponse) throws IOException {
        var classifier = (AbstractClassifier) classificationResult.getEvaluationResults().getClassifier();
        uploadModel(classifier, evaluationLog);
        String modelUrl = getModelPresignedUrl(evaluationLog);
        evaluationLog.setRequestStatus(RequestStatus.FINISHED);
        evaluationResponse.setEvaluationResults(classificationResult.getEvaluationResults());
        evaluationResponse.setModelUrl(modelUrl);
        evaluationResponse.setStatus(TechnicalStatus.SUCCESS);
    }

    private void handleError(EvaluationLog evaluationLog, EvaluationResponse evaluationResponse, String errorMessage) {
        evaluationLog.setRequestStatus(RequestStatus.ERROR);
        evaluationLog.setErrorMessage(errorMessage);
        evaluationResponse.setStatus(TechnicalStatus.ERROR);
        MessageError error = error(ErrorCode.INTERNAL_SERVER_ERROR);
        evaluationResponse.setErrors(Collections.singletonList(error));
    }
}
