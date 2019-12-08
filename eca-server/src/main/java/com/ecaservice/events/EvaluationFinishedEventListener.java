package com.ecaservice.events;

import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.events.model.EvaluationFinishedEvent;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.service.ers.ErsRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Classifier evaluation finished event listener.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EvaluationFinishedEventListener {

    private final ErsRequestService ersRequestService;
    private final EvaluationLogRepository evaluationLogRepository;

    /**
     * Handles event to sent evaluation results to ERS.
     *
     * @param evaluationFinishedEvent - evaluation finished event
     */
    @Async("ecaThreadPoolTaskExecutor")
    @EventListener
    public void handleEvaluationFinishedEvent(EvaluationFinishedEvent evaluationFinishedEvent) {
        EvaluationResponse evaluationResponse = evaluationFinishedEvent.getEvaluationResponse();
        log.info("Handles event to sent evaluation results to ERS for request id [{}]",
                evaluationResponse.getRequestId());
        EvaluationLog evaluationLog =
                evaluationLogRepository.findByRequestIdAndEvaluationStatusIn(evaluationResponse.getRequestId(),
                        Collections.singletonList(RequestStatus.FINISHED));
        if (evaluationLog != null) {
            EvaluationResultsRequestEntity requestEntity = new EvaluationResultsRequestEntity();
            requestEntity.setEvaluationLog(evaluationLog);
            ersRequestService.saveEvaluationResults(evaluationResponse.getEvaluationResults(), requestEntity);
        }
    }
}
