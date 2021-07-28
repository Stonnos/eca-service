package com.ecaservice.auto.test.mq.listener;

import com.ecaservice.auto.test.repository.ExperimentRequestRepository;
import com.ecaservice.base.model.EcaResponse;
import com.ecaservice.test.common.model.RequestStageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Implements rabbit message listener.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMessageListener {

    private final ExperimentRequestRepository experimentRequestRepository;

    /**
     * Handles response messages from eca - server.
     *
     * @param ecaResponse - eca response
     * @param message     - original message
     */
    @RabbitListener(queues = "${queue.experimentReplyToQueue}")
    public void handleMessage(EcaResponse ecaResponse, Message message) {
        String correlationId = message.getMessageProperties().getCorrelationId();
        log.info("Received message with correlation id [{}]", correlationId);
        var experimentRequestEntity = experimentRequestRepository.findByCorrelationId(correlationId);
        if (experimentRequestEntity == null) {
            log.warn("Can't find request entity with correlation id [{}]", correlationId);
            return;
        }
        if (RequestStageType.EXCEEDED.equals(experimentRequestEntity.getStageType())) {
            log.warn("Got exceeded request entity with correlation id [{}]", correlationId);
        } else {
            experimentRequestEntity.setStageType(RequestStageType.RESPONSE_RECEIVED);
            experimentRequestEntity.setRequestId(ecaResponse.getRequestId());
            experimentRequestRepository.save(experimentRequestEntity);
            log.info("Message [{}] response has been processed", correlationId);
        }
    }
}
