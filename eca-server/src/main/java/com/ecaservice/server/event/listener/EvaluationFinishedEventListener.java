package com.ecaservice.server.event.listener;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.event.model.EvaluationFinishedEvent;
import com.ecaservice.server.model.ErsEvaluationRequestData;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.ers.ErsRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

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
    @EventListener
    public void handleEvaluationFinishedEvent(EvaluationFinishedEvent evaluationFinishedEvent) {
        EvaluationResponse evaluationResponse = evaluationFinishedEvent.getEvaluationResponse();
        log.info("Handles event to sent evaluation results to ERS for request id [{}]",
                evaluationResponse.getRequestId());
        if (!TechnicalStatus.SUCCESS.equals(evaluationResponse.getStatus())) {
            log.warn("Can't send evaluation [{}] results to ERS in technical status [{}]",
                    evaluationResponse.getRequestId(), evaluationResponse.getStatus());
        } else {
            EvaluationLog evaluationLog = evaluationLogRepository.findByRequestId(evaluationResponse.getRequestId())
                    .orElseThrow(
                            () -> new EntityNotFoundException(EvaluationLog.class, evaluationResponse.getRequestId()));
            if (!RequestStatus.FINISHED.equals(evaluationLog.getRequestStatus())) {
                log.warn("Can't send evaluation [{}] results to ERS in request status [{}]",
                        evaluationLog.getRequestId(),
                        evaluationLog.getRequestStatus());
            } else {
                var evaluationResultsRequestEntity = new EvaluationResultsRequestEntity();
                evaluationResultsRequestEntity.setEvaluationLog(evaluationLog);
                var ersEvaluationRequestData = ErsEvaluationRequestData.builder()
                        .ersRequest(evaluationResultsRequestEntity)
                        .evaluationEntity(evaluationLog)
                        .evaluationResults(evaluationResponse.getEvaluationResults())
                        .build();
                ersRequestService.saveEvaluationResults(ersEvaluationRequestData);
            }
        }
    }
}
