package com.ecaservice.listener;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.InstancesRequest;
import com.ecaservice.config.rabbit.Queues;
import com.ecaservice.event.model.EvaluationFinishedEvent;
import com.ecaservice.service.evaluation.EvaluationOptimizerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

/**
 * Rabbit MQ listener for evaluation optimizer request messages.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EvaluationOptimizerRequestListener {

    private final RabbitTemplate rabbitTemplate;
    private final EvaluationOptimizerService evaluationOptimizerService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Handles evaluation optimizer request message.
     *
     * @param instancesRequest - instances request
     */
    @RabbitListener(queues = Queues.EVALUATION_OPTIMIZER_REQUEST_QUEUE)
    public void handleMessage(@Valid @Payload InstancesRequest instancesRequest, Message inboundMessage) {
        EvaluationResponse evaluationResponse =
                evaluationOptimizerService.evaluateWithOptimalClassifierOptions(instancesRequest);
        log.info("Evaluation response [{}] with status [{}] has been built for evaluation optimizer request.",
                evaluationResponse.getRequestId(), evaluationResponse.getStatus());
        eventPublisher.publishEvent(new EvaluationFinishedEvent(this, evaluationResponse));
        MessageProperties inboundMessageProperties = inboundMessage.getMessageProperties();
        rabbitTemplate.convertAndSend(inboundMessageProperties.getReplyTo(), evaluationResponse, outboundMessage -> {
            outboundMessage.getMessageProperties().setCorrelationId(inboundMessageProperties.getCorrelationId());
            return outboundMessage;
        });
    }
}
