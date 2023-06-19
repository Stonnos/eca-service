package com.ecaservice.server.event.listener;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.event.model.EvaluationErsReportEvent;
import com.ecaservice.server.model.ErsEvaluationRequestData;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.model.evaluation.EvaluationResultsDataModel;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.ers.ErsRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Event listener to sent evaluation results to ERS.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EvaluationErsReportEventListener {

    private final ErsRequestService ersRequestService;
    private final EvaluationLogRepository evaluationLogRepository;

    /**
     * Handles event to sent evaluation results to ERS.
     *
     * @param evaluationErsReportEvent - evaluation finished event
     */
    @EventListener
    public void handleEvent(EvaluationErsReportEvent evaluationErsReportEvent) {
        EvaluationResultsDataModel evaluationResultsDataModel =
                evaluationErsReportEvent.getEvaluationResultsDataModel();
        log.info("Handles event to sent evaluation results to ERS for request id [{}]",
                evaluationResultsDataModel.getRequestId());
        if (!RequestStatus.FINISHED.equals(evaluationResultsDataModel.getStatus())) {
            log.warn("Can't send evaluation [{}] results to ERS with request status [{}]",
                    evaluationResultsDataModel.getRequestId(), evaluationResultsDataModel.getStatus());
        } else {
            var evaluationLog = getEvaluationLog(evaluationResultsDataModel.getRequestId());
            var evaluationResultsRequestEntity = new EvaluationResultsRequestEntity();
            evaluationResultsRequestEntity.setEvaluationLog(evaluationLog);
            var ersEvaluationRequestData = ErsEvaluationRequestData.builder()
                    .ersRequest(evaluationResultsRequestEntity)
                    .evaluationEntity(evaluationLog)
                    .evaluationResults(evaluationResultsDataModel.getEvaluationResults())
                    .build();
            ersRequestService.saveEvaluationResults(ersEvaluationRequestData);
        }
    }

    private EvaluationLog getEvaluationLog(String requestId) {
        return evaluationLogRepository.findByRequestId(requestId)
                .orElseThrow(() -> new EntityNotFoundException(EvaluationLog.class, requestId));
    }
}
