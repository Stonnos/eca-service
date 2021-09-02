package com.ecaservice.event.listener;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.event.model.EvaluationFinishedEvent;
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

import static com.ecaservice.config.EcaServiceConfiguration.ECA_THREAD_POOL_TASK_EXECUTOR;

/**
 * Event listener that occurs after evaluation is finished.
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
    @Async(ECA_THREAD_POOL_TASK_EXECUTOR)
    @EventListener
    public void handleEvaluationFinishedEvent(EvaluationFinishedEvent evaluationFinishedEvent) {
        EvaluationResponse evaluationResponse = evaluationFinishedEvent.getEvaluationResponse();
        log.info("Handles event to sent evaluation results to ERS for request id [{}]",
                evaluationResponse.getRequestId());
        if (!TechnicalStatus.SUCCESS.equals(evaluationResponse.getStatus())) {
            log.warn("Can't send evaluation [{}] results to ERS in technical status [{}]",
                    evaluationResponse.getRequestId(), evaluationResponse.getStatus());
        }
        EvaluationLog evaluationLog = evaluationLogRepository.findByRequestId(evaluationResponse.getRequestId())
                .orElseThrow(() -> new EntityNotFoundException(EvaluationLog.class, evaluationResponse.getRequestId()));
        if (!RequestStatus.FINISHED.equals(evaluationLog.getRequestStatus())) {
            log.warn("Can't send evaluation [{}] results to ERS in request status [{}]", evaluationLog.getRequestId(),
                    evaluationLog.getRequestStatus());
        } else {
            EvaluationResultsRequestEntity requestEntity = new EvaluationResultsRequestEntity();
            requestEntity.setEvaluationLog(evaluationLog);
            ersRequestService.saveEvaluationResults(evaluationResponse.getEvaluationResults(), requestEntity);
        }
    }
}
