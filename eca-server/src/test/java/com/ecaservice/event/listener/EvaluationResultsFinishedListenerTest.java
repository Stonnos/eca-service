package com.ecaservice.event.listener;

import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.event.model.EvaluationFinishedEvent;
import com.ecaservice.model.entity.ErsRequest;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.service.ers.ErsRequestService;
import eca.core.evaluation.EvaluationResults;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.UUID;

import static com.ecaservice.TestHelperUtils.createEvaluationLog;
import static com.ecaservice.TestHelperUtils.createEvaluationResponse;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for checking {@see EvaluationFinishedEventListener} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
public class EvaluationResultsFinishedListenerTest {

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
    public void testEvaluationResultsSentWithFinishedStatus() {
        EvaluationLog evaluationLog = createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        when(evaluationLogRepository.findByRequestIdAndEvaluationStatusIn(evaluationLog.getRequestId(),
                Collections.singletonList(RequestStatus.FINISHED))).thenReturn(evaluationLog);
        EvaluationResponse evaluationResponse = createEvaluationResponse(evaluationLog.getRequestId());
        EvaluationFinishedEvent evaluationFinishedEvent = new EvaluationFinishedEvent(this, evaluationResponse);
        evaluationFinishedEventListener.handleEvaluationFinishedEvent(evaluationFinishedEvent);
        verify(ersRequestService, atLeastOnce())
                .saveEvaluationResults(any(EvaluationResults.class), any(ErsRequest.class));
    }

    /**
     * Test evaluation results sent for evaluation log with status ERROR.
     */
    @Test
    public void testEvaluationResultsSentWithErrorStatus() {
        EvaluationLog evaluationLog = createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.ERROR);
        when(evaluationLogRepository.findByRequestIdAndEvaluationStatusIn(evaluationLog.getRequestId(),
                Collections.singletonList(RequestStatus.FINISHED))).thenReturn(null);
        EvaluationResponse evaluationResponse = createEvaluationResponse(evaluationLog.getRequestId());
        EvaluationFinishedEvent evaluationFinishedEvent = new EvaluationFinishedEvent(this, evaluationResponse);
        evaluationFinishedEventListener.handleEvaluationFinishedEvent(evaluationFinishedEvent);
        verify(ersRequestService, never())
                .saveEvaluationResults(any(EvaluationResults.class), any(ErsRequest.class));
    }
}
