package com.ecaservice.load.test.service.rabbit;

import com.ecaservice.load.test.config.rabbit.QueueConfig;
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
     * Sends message to rabbit mq.
     *
     * @param payload       - message payload
     * @param correlationId - message correlation id
     */
    public void send(Object payload, String correlationId) {
        rabbitTemplate.convertAndSend(queueConfig.getEvaluationRequestQueue(), payload, message -> {
            message.getMessageProperties().setCorrelationId(correlationId);
            message.getMessageProperties().setReplyTo(queueConfig.getReplyToQueue());
            return message;
        });
    }
}
