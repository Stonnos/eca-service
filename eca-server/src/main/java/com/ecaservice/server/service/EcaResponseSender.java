package com.ecaservice.server.service;

import com.ecaservice.base.model.EcaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Eca response sender.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EcaResponseSender {

    private final AmqpAdmin amqpAdmin;
    private final RabbitTemplate rabbitTemplate;

    /**
     * Sends eca response.
     *
     * @param ecaResponse   - eca response
     * @param correlationId - correlation id header
     * @param replyTo       - reply to queue
     */
    public void sendResponse(EcaResponse ecaResponse, String correlationId, String replyTo) {
        log.info("Starting to sent eca [{}] response with status [{}] to MQ", ecaResponse.getRequestId(),
                ecaResponse.getStatus());
        QueueInformation queueInformation = amqpAdmin.getQueueInfo(replyTo);
        if (queueInformation == null) {
            log.warn("Can't sent eca [{}] response with status [{}], because reply to queue doesn't exists",
                    ecaResponse.getRequestId(), ecaResponse.getStatus());
        } else {
            rabbitTemplate.convertAndSend(replyTo, ecaResponse, outboundMessage -> {
                outboundMessage.getMessageProperties().setCorrelationId(correlationId);
                return outboundMessage;
            });
            log.info("Eca [{}] response with status [{}] has been sent to MQ", ecaResponse.getRequestId(),
                    ecaResponse.getStatus());
        }
    }
}
