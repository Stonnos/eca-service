package com.ecaservice.external.api.service;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.external.api.config.rabbit.QueueConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Rabbit MQ message sender.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitSender {

    private final QueueConfig queueConfig;
    private final RabbitTemplate rabbitTemplate;

    /**
     * Sends evaluation request message to rabbit mq.
     *
     * @param evaluationRequest - evaluation request
     * @param correlationId     - message correlation id
     */
    public void sendEvaluationRequest(EvaluationRequest evaluationRequest, String correlationId) {
        log.debug("Starting to send evaluation request [{}] to rabbit mq", correlationId);
        rabbitTemplate.convertAndSend(queueConfig.getEvaluationRequestQueue(), evaluationRequest, message -> {
            message.getMessageProperties().setCorrelationId(correlationId);
            message.getMessageProperties().setReplyTo(queueConfig.getEvaluationRequestReplyToQueue());
            return message;
        });
        log.debug("Evaluation request [{}] has been to rabbit mq", correlationId);
    }
}
