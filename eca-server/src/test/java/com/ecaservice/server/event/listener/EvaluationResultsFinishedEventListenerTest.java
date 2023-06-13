package com.ecaservice.server.event.listener;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.server.event.model.EvaluationFinishedEvent;
import com.ecaservice.server.model.ErsEvaluationRequestData;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.ers.ErsRequestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.createEvaluationLog;
import static com.ecaservice.server.TestHelperUtils.createEvaluationResponse;
import static com.ecaservice.server.TestHelperUtils.getEvaluationResults;
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
        verify(ersRequestService, atLeastOnce()).saveEvaluationResults(any(ErsEvaluationRequestData.class));
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
        verify(ersRequestService, never()).saveEvaluationResults(any(ErsEvaluationRequestData.class));
    }
}
