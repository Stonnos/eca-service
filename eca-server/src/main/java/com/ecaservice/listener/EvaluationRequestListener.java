package com.ecaservice.listener;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.event.model.EvaluationFinishedEvent;
import com.ecaservice.service.evaluation.EvaluationRequestService;
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
 * Rabbit MQ listener for evaluation request messages.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EvaluationRequestListener {

    private final RabbitTemplate rabbitTemplate;
    private final EvaluationRequestService evaluationRequestService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Handles evaluation request message.
     *
     * @param evaluationRequest - evaluation request
     */
    @RabbitListener(queues = "${queue.evaluationRequestQueue}")
    public void handleMessage(@Valid @Payload EvaluationRequest evaluationRequest, Message inboundMessage) {
        EvaluationResponse evaluationResponse = evaluationRequestService.processRequest(evaluationRequest);
        log.info("Evaluation response [{}] with status [{}] has been built.", evaluationResponse.getRequestId(),
                evaluationResponse.getStatus());
        eventPublisher.publishEvent(new EvaluationFinishedEvent(this, evaluationResponse));
        MessageProperties inboundMessageProperties = inboundMessage.getMessageProperties();
        rabbitTemplate.convertAndSend(inboundMessageProperties.getReplyTo(), evaluationResponse, outboundMessage -> {
            outboundMessage.getMessageProperties().setCorrelationId(inboundMessageProperties.getCorrelationId());
            return outboundMessage;
        });
    }
}
