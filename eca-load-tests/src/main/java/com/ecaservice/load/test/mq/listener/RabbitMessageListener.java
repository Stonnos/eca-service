package com.ecaservice.load.test.mq.listener;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.load.test.entity.EvaluationRequestEntity;
import com.ecaservice.load.test.entity.RequestStageType;
import com.ecaservice.load.test.mapping.TestResultMapper;
import com.ecaservice.load.test.repository.EvaluationRequestRepository;
import com.ecaservice.test.common.model.TestResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Implements rabbit message listener.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMessageListener {

    private final TestResultMapper testResultMapper;
    private final EvaluationRequestRepository evaluationRequestRepository;

    /**
     * Handles response messages from eca - server.
     *
     * @param evaluationResponse - evaluation response
     * @param message            - message
     */
    @RabbitListener(queues = "${queue.replyToQueue}")
    public void handleMessage(EvaluationResponse evaluationResponse, Message message) {
        String correlationId = message.getMessageProperties().getCorrelationId();
        log.trace("Received message with correlation id [{}]", correlationId);
        EvaluationRequestEntity evaluationRequestEntity =
                evaluationRequestRepository.findByCorrelationId(correlationId);
        if (evaluationRequestEntity == null) {
            log.warn("Can't find request entity with correlation id [{}]", correlationId);
            return;
        }
        if (RequestStageType.EXCEEDED.equals(evaluationRequestEntity.getStageType())) {
            log.warn("Got exceeded request entity with correlation id [{}]", correlationId);
        } else {
            try {
                evaluationRequestEntity.setTestResult(testResultMapper.map(evaluationResponse.getStatus()));
                evaluationRequestEntity.setStageType(RequestStageType.COMPLETED);
                log.trace("Request with correlation id [{}] has been processed", correlationId);
            } catch (Exception ex) {
                log.error("There was an error while handle message [{}]: {}", correlationId, ex.getMessage());
                evaluationRequestEntity.setStageType(RequestStageType.ERROR);
                evaluationRequestEntity.setTestResult(TestResult.ERROR);
            } finally {
                evaluationRequestEntity.setFinished(LocalDateTime.now());
                evaluationRequestRepository.save(evaluationRequestEntity);
            }
        }
    }
}
