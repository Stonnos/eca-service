package com.ecaservice.load.test.service;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.load.test.entity.EvaluationRequestEntity;
import com.ecaservice.load.test.entity.RequestStageType;
import com.ecaservice.load.test.entity.TestResult;
import com.ecaservice.load.test.repository.EvaluationRequestRepository;
import com.ecaservice.load.test.service.rabbit.RabbitSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;

/**
 * Test worker service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestWorkerService {

    private final RabbitSender rabbitSender;
    private final EvaluationRequestRepository evaluationRequestRepository;

    /**
     * Sends evaluation test request to broker.
     *
     * @param testId            - test id
     * @param evaluationRequest - evaluation request
     * @param countDownLatch    - count down latch
     */
    public void sendRequest(long testId, EvaluationRequest evaluationRequest, CountDownLatch countDownLatch) {
        EvaluationRequestEntity evaluationRequestEntity = evaluationRequestRepository.findById(testId).orElseThrow(
                () -> new EntityNotFoundException(EvaluationRequestEntity.class, testId));
        try {
            evaluationRequestEntity.setStarted(LocalDateTime.now());
            rabbitSender.send(evaluationRequest, evaluationRequestEntity.getCorrelationId());
            evaluationRequestEntity.setStageType(RequestStageType.REQUEST_SENT);
            log.trace("Request with correlation id [{}] has been sent", evaluationRequestEntity.getCorrelationId());
        } catch (Exception ex) {
            log.error("Unknown error while sending request with correlation id [{}]: {}",
                    evaluationRequestEntity.getCorrelationId(), ex.getMessage());
            handleErrorRequest(evaluationRequestEntity);
        } finally {
            evaluationRequestRepository.save(evaluationRequestEntity);
            countDownLatch.countDown();
        }
    }

    private void handleErrorRequest(EvaluationRequestEntity evaluationRequestEntity) {
        evaluationRequestEntity.setTestResult(TestResult.ERROR);
        evaluationRequestEntity.setStageType(RequestStageType.ERROR);
        evaluationRequestEntity.setFinished(LocalDateTime.now());
    }
}
