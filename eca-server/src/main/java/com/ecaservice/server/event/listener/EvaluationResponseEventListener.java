package com.ecaservice.server.event.listener;

import com.ecaservice.server.event.model.EvaluationResponseEvent;
import com.ecaservice.server.mapping.EcaResponseMapper;
import com.ecaservice.server.model.evaluation.EvaluationResultsDataModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Evaluation response event listener.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EvaluationResponseEventListener {

    private final RabbitTemplate rabbitTemplate;
    private final EcaResponseMapper ecaResponseMapper;

    /**
     * Handles event to sent evaluation response to MQ.
     *
     * @param evaluationResponseEvent - evaluation response event
     */
    @EventListener
    public void handleEvaluationResponseEvent(EvaluationResponseEvent evaluationResponseEvent) {
        EvaluationResultsDataModel evaluationResultsDataModel =
                evaluationResponseEvent.getEvaluationResultsDataModel();
        var evaluationResponse = ecaResponseMapper.map(evaluationResultsDataModel);
        log.info("Starting to sent evaluation [{}] response with request status [{}] to MQ",
                evaluationResponse.getRequestId(), evaluationResponse.getStatus());
        rabbitTemplate.convertAndSend(evaluationResponseEvent.getReplyTo(), evaluationResponse, outboundMessage -> {
            outboundMessage.getMessageProperties().setCorrelationId(evaluationResponseEvent.getCorrelationId());
            return outboundMessage;
        });
        log.info("Evaluation [{}] response with request status [{}] has been sent to MQ",
                evaluationResponse.getRequestId(), evaluationResponse.getStatus());
    }
}
