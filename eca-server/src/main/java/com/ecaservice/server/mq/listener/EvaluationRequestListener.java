package com.ecaservice.server.mq.listener;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.server.event.model.EvaluationFinishedEvent;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.service.evaluation.EvaluationRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(value = "rabbit.enabled", havingValue = "true")
@RequiredArgsConstructor
public class EvaluationRequestListener {

    private final RabbitTemplate rabbitTemplate;
    private final EvaluationRequestService evaluationRequestService;
    private final ApplicationEventPublisher eventPublisher;
    private final EvaluationLogMapper evaluationLogMapper;

    /**
     * Handles evaluation request message.
     *
     * @param evaluationRequest - evaluation request
     */
    @RabbitListener(queues = "${queue.evaluationRequestQueue}")
    public void handleMessage(@Valid @Payload EvaluationRequest evaluationRequest, Message inboundMessage) {
        var evaluationRequestDataModel = evaluationLogMapper.map(evaluationRequest);
        EvaluationResponse evaluationResponse = evaluationRequestService.processRequest(evaluationRequestDataModel);
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
