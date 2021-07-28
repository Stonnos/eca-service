package com.ecaservice.auto.test.service;

import com.ecaservice.auto.test.entity.ExperimentRequestEntity;
import com.ecaservice.auto.test.repository.ExperimentRequestRepository;
import com.ecaservice.auto.test.service.rabbit.RabbitSender;
import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.model.RequestStageType;
import com.ecaservice.test.common.model.TestResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Test worker service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AutoTestWorkerService {

    private final RabbitSender rabbitSender;
    private final ExperimentRequestRepository experimentRequestRepository;

    /**
     * Sends experiment test request to broker.
     *
     * @param testId            - test id
     * @param experimentRequest - experiment request
     */
    public void sendRequest(long testId, ExperimentRequest experimentRequest) {
        var experimentRequestEntity = experimentRequestRepository.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException(ExperimentRequestEntity.class, testId));
        log.info("Starting to send experiment request with correlation id [{}]",
                experimentRequestEntity.getCorrelationId());
        try {
            experimentRequestEntity.setStarted(LocalDateTime.now());
            rabbitSender.sendExperimentRequest(experimentRequest, experimentRequestEntity.getCorrelationId());
            experimentRequestEntity.setStageType(RequestStageType.REQUEST_SENT);
            experimentRequestEntity.setExecutionStatus(ExecutionStatus.IN_PROGRESS);
            log.trace("Experiment request with correlation id [{}] has been sent",
                    experimentRequestEntity.getCorrelationId());
        } catch (Exception ex) {
            log.error("Unknown error while sending request with correlation id [{}]: {}",
                    experimentRequestEntity.getCorrelationId(), ex.getMessage());
            handleErrorRequest(experimentRequestEntity);
        } finally {
            experimentRequestRepository.save(experimentRequestEntity);
        }
    }

    private void handleErrorRequest(ExperimentRequestEntity evaluationRequestEntity) {
        evaluationRequestEntity.setTestResult(TestResult.ERROR);
        evaluationRequestEntity.setStageType(RequestStageType.ERROR);
        evaluationRequestEntity.setExecutionStatus(ExecutionStatus.ERROR);
        evaluationRequestEntity.setFinished(LocalDateTime.now());
    }
}
