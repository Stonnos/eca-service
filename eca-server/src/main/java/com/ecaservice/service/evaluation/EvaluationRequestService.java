package com.ecaservice.service.evaluation;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.MessageError;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.mapping.EvaluationLogMapper;
import com.ecaservice.model.entity.AppInstanceEntity;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.evaluation.ClassificationResult;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.service.AppInstanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.ecaservice.common.web.logging.LogHelper.EV_REQUEST_ID;
import static com.ecaservice.common.web.logging.LogHelper.TX_ID;
import static com.ecaservice.common.web.logging.LogHelper.putMdc;
import static com.ecaservice.common.web.logging.LogHelper.putMdcIfAbsent;
import static com.ecaservice.util.Utils.error;

/**
 * Evaluation request service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationRequestService {

    private final CrossValidationConfig crossValidationConfig;
    private final CalculationExecutorService executorService;
    private final EvaluationService evaluationService;
    private final AppInstanceService appInstanceService;
    private final EvaluationLogRepository evaluationLogRepository;
    private final EvaluationLogMapper evaluationLogMapper;

    /**
     * Processes input request and returns classification results.
     *
     * @param evaluationRequest - evaluation request
     * @return evaluation response
     */
    public EvaluationResponse processRequest(final EvaluationRequest evaluationRequest) {
        String requestId = UUID.randomUUID().toString();
        putMdcIfAbsent(TX_ID, requestId);
        putMdc(EV_REQUEST_ID, requestId);
        log.info("Received request for classifier [{}] evaluation with data [{}]",
                evaluationRequest.getClassifier().getClass().getSimpleName(),
                evaluationRequest.getData().relationName());
        AppInstanceEntity appInstanceEntity = appInstanceService.getAppInstanceEntity();
        EvaluationLog evaluationLog = evaluationLogMapper.map(evaluationRequest, crossValidationConfig);
        evaluationLog.setRequestStatus(RequestStatus.IN_PROGRESS);
        evaluationLog.setRequestId(requestId);
        evaluationLog.setAppInstanceEntity(appInstanceEntity);
        evaluationLog.setCreationDate(LocalDateTime.now());
        evaluationLog.setStartDate(LocalDateTime.now());
        evaluationLogRepository.save(evaluationLog);
        EvaluationResponse evaluationResponse = new EvaluationResponse();
        evaluationResponse.setRequestId(evaluationLog.getRequestId());
        try {
            Callable<ClassificationResult> callable = () -> evaluationService.evaluateModel(evaluationRequest);
            ClassificationResult classificationResult = executorService.execute(callable,
                    crossValidationConfig.getTimeout(), TimeUnit.MINUTES);

            if (classificationResult.isSuccess()) {
                evaluationLog.setRequestStatus(RequestStatus.FINISHED);
                evaluationResponse.setEvaluationResults(classificationResult.getEvaluationResults());
                evaluationResponse.setStatus(TechnicalStatus.SUCCESS);
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

    private void handleError(EvaluationLog evaluationLog, EvaluationResponse evaluationResponse, String errorMessage) {
        evaluationLog.setRequestStatus(RequestStatus.ERROR);
        evaluationLog.setErrorMessage(errorMessage);
        evaluationResponse.setStatus(TechnicalStatus.ERROR);
        MessageError error = error(errorMessage);
        evaluationResponse.setErrors(Collections.singletonList(error));
    }
}
