package com.ecaservice.auto.test.service;

import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import com.ecaservice.auto.test.entity.autotest.RequestStageType;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.auto.test.service.rabbit.RabbitSender;
import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.model.TestResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.function.BiConsumer;

/**
 * Auto test worker service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AutoTestWorkerService {

    private final RabbitSender rabbitSender;
    private final BaseEvaluationRequestRepository baseEvaluationRequestRepository;

    /**
     * Sends experiment test request to mq.
     *
     * @param testId            - test id
     * @param experimentRequest - experiment request
     */
    public void sendRequest(long testId, ExperimentRequest experimentRequest) {
        internalSendRequest(testId, experimentRequest, rabbitSender::sendExperimentRequest);
    }

    /**
     * Sends evaluation test request to mq.
     *
     * @param testId            - test id
     * @param evaluationRequest - evaluation request
     */
    public void sendRequest(long testId, EvaluationRequest evaluationRequest) {
        internalSendRequest(testId, evaluationRequest, rabbitSender::sendEvaluationRequest);
    }

    private <R> void internalSendRequest(long testId, R request, BiConsumer<R, String> sender) {
        var baseEvaluationRequestEntity = baseEvaluationRequestRepository.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException(ExperimentRequestEntity.class, testId));
        log.info("Starting to send request with correlation id [{}]", baseEvaluationRequestEntity.getCorrelationId());
        try {
            baseEvaluationRequestEntity.setStarted(LocalDateTime.now());
            sender.accept(request, baseEvaluationRequestEntity.getCorrelationId());
            baseEvaluationRequestEntity.setStageType(RequestStageType.REQUEST_SENT);
            baseEvaluationRequestEntity.setExecutionStatus(ExecutionStatus.IN_PROGRESS);
            log.info("Request with correlation id [{}] has been sent", baseEvaluationRequestEntity.getCorrelationId());
        } catch (Exception ex) {
            log.error("Unknown error while sending request with correlation id [{}]: {}",
                    baseEvaluationRequestEntity.getCorrelationId(), ex.getMessage());
            handleErrorRequest(baseEvaluationRequestEntity, ex);
        } finally {
            baseEvaluationRequestRepository.save(baseEvaluationRequestEntity);
        }
    }

    private void handleErrorRequest(BaseEvaluationRequestEntity baseEvaluationRequestEntity, Exception ex) {
        baseEvaluationRequestEntity.setTestResult(TestResult.ERROR);
        baseEvaluationRequestEntity.setStageType(RequestStageType.ERROR);
        baseEvaluationRequestEntity.setExecutionStatus(ExecutionStatus.ERROR);
        baseEvaluationRequestEntity.setDetails(ex.getMessage());
        baseEvaluationRequestEntity.setFinished(LocalDateTime.now());
    }
}
