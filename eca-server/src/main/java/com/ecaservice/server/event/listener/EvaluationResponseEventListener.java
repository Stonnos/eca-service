package com.ecaservice.server.event.listener;

import com.ecaservice.server.event.model.EvaluationResponseEvent;
import com.ecaservice.server.mapping.EcaResponseMapper;
import com.ecaservice.server.service.EcaResponseSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final EcaResponseSender ecaResponseSender;
    private final EcaResponseMapper ecaResponseMapper;

    /**
     * Handles event to sent evaluation response to MQ.
     *
     * @param evaluationResponseEvent - evaluation response event
     */
    @EventListener
    public void handleEvaluationResponseEvent(EvaluationResponseEvent evaluationResponseEvent) {
        var evaluationResultsModel = evaluationResponseEvent.getEvaluationResultsModel();
        var evaluationResponse = ecaResponseMapper.map(evaluationResultsModel);
        log.info("Starting to sent evaluation [{}] response with request status [{}]",
                evaluationResponse.getRequestId(), evaluationResponse.getStatus());
        ecaResponseSender.sendResponse(evaluationResponse, evaluationResponseEvent.getCorrelationId(),
                evaluationResponseEvent.getReplyTo());
        log.info("Evaluation [{}] response with request status [{}] has been sent",
                evaluationResponse.getRequestId(), evaluationResponse.getStatus());
    }
}
