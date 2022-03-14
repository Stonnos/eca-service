package com.ecaservice.auto.test.service.rabbit;

import com.ecaservice.auto.test.config.rabbit.QueueConfig;
import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.base.model.ExperimentRequest;
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
     * Sends experiment request message to rabbit mq.
     *
     * @param experimentRequest - message payload
     * @param correlationId     - message correlation id
     */
    public void sendExperimentRequest(ExperimentRequest experimentRequest, String correlationId) {
        log.debug("Starting to sent experiment request with correlation id [{}] to queue", correlationId);
        rabbitTemplate.convertAndSend(queueConfig.getExperimentRequestQueue(), experimentRequest, message -> {
            message.getMessageProperties().setCorrelationId(correlationId);
            message.getMessageProperties().setReplyTo(queueConfig.getExperimentReplyToQueue());
            return message;
        });
        log.debug("Experiment request with correlation id [{}] has been sent to queue", correlationId);
    }

    /**
     * Sends evaluation request message to rabbit mq.
     *
     * @param evaluationRequest - message payload
     * @param correlationId     - message correlation id
     */
    public void sendEvaluationRequest(EvaluationRequest evaluationRequest, String correlationId) {
        log.debug("Starting to sent evaluation request with correlation id [{}] to queue", correlationId);
        rabbitTemplate.convertAndSend(queueConfig.getEvaluationRequestQueue(), evaluationRequest, message -> {
            message.getMessageProperties().setCorrelationId(correlationId);
            message.getMessageProperties().setReplyTo(queueConfig.getEvaluationReplyToQueue());
            return message;
        });
        log.debug("Evaluation request with correlation id [{}] has been sent to queue", correlationId);
    }
}
