package com.ecaservice.event.listener;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.event.model.EvaluationFinishedEvent;
import com.ecaservice.model.entity.ErsRequest;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.service.ers.ErsRequestService;
import eca.core.evaluation.EvaluationResults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.ecaservice.TestHelperUtils.createEvaluationLog;
import static com.ecaservice.TestHelperUtils.createEvaluationResponse;
import static com.ecaservice.TestHelperUtils.getEvaluationResults;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@see EvaluationFinishedEventListener} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class EvaluationResultsFinishedEventListenerTest {

    @Mock
    private ErsRequestService ersRequestService;
    @Mock
    private EvaluationLogRepository evaluationLogRepository;
    @InjectMocks
    private EvaluationFinishedEventListener evaluationFinishedEventListener;

    /**
     * Test evaluation results sent for evaluation log with status FINISHED.
     */
    @Test
    void testEvaluationResultsSentWithFinishedStatus() {
        EvaluationLog evaluationLog = createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        when(evaluationLogRepository.findByRequestId(evaluationLog.getRequestId())).thenReturn(
                Optional.of(evaluationLog));
        EvaluationResponse evaluationResponse = createEvaluationResponse(evaluationLog.getRequestId());
        evaluationResponse.setEvaluationResults(getEvaluationResults());
        EvaluationFinishedEvent evaluationFinishedEvent = new EvaluationFinishedEvent(this, evaluationResponse);
        evaluationFinishedEventListener.handleEvaluationFinishedEvent(evaluationFinishedEvent);
        verify(ersRequestService, atLeastOnce())
                .saveEvaluationResults(any(EvaluationResults.class), any(ErsRequest.class));
    }

    /**
     * Test evaluation results sent for evaluation log with status ERROR.
     */
    @Test
    void testEvaluationResultsSentWithErrorStatus() {
        EvaluationLog evaluationLog = createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.ERROR);
        when(evaluationLogRepository.findByRequestId(evaluationLog.getRequestId())).thenReturn(
                Optional.of(evaluationLog));
        EvaluationResponse evaluationResponse = createEvaluationResponse(evaluationLog.getRequestId());
        EvaluationFinishedEvent evaluationFinishedEvent = new EvaluationFinishedEvent(this, evaluationResponse);
        evaluationFinishedEventListener.handleEvaluationFinishedEvent(evaluationFinishedEvent);
        verify(ersRequestService, never())
                .saveEvaluationResults(any(EvaluationResults.class), any(ErsRequest.class));
    }
}
