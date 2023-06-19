package com.ecaservice.server.event.listener;

import com.ecaservice.server.event.model.EvaluationErsReportEvent;
import com.ecaservice.server.model.ErsEvaluationRequestData;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.model.evaluation.EvaluationResultsDataModel;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.ers.ErsRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.createEvaluationLog;
import static com.ecaservice.server.TestHelperUtils.createEvaluationResultsDataModel;
import static com.ecaservice.server.TestHelperUtils.getEvaluationResults;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link EvaluationErsReportEventListener} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
class EvaluationErsReportEventListenerTest {

    @MockBean
    private ErsRequestService ersRequestService;
    @MockBean
    private EvaluationLogRepository evaluationLogRepository;

    private EvaluationErsReportEventListener evaluationErsReportEventListener;

    @BeforeEach
    void init() {
        evaluationErsReportEventListener = new EvaluationErsReportEventListener(ersRequestService,
                evaluationLogRepository);
    }

    /**
     * Test evaluation results sent for evaluation log with status FINISHED.
     */
    @Test
    void testEvaluationResultsSentWithFinishedStatus() {
        EvaluationLog evaluationLog = createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        when(evaluationLogRepository.findByRequestId(evaluationLog.getRequestId())).thenReturn(
                Optional.of(evaluationLog));
        EvaluationResultsDataModel evaluationResponse =
                createEvaluationResultsDataModel(evaluationLog.getRequestId());
        evaluationResponse.setEvaluationResults(getEvaluationResults());
        EvaluationErsReportEvent evaluationErsReportEvent = new EvaluationErsReportEvent(this, evaluationResponse);
        evaluationErsReportEventListener.handleEvent(evaluationErsReportEvent);
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
        EvaluationResultsDataModel evaluationResponse =
                createEvaluationResultsDataModel(evaluationLog.getRequestId());
        evaluationResponse.setStatus(RequestStatus.ERROR);
        EvaluationErsReportEvent evaluationErsReportEvent = new EvaluationErsReportEvent(this, evaluationResponse);
        evaluationErsReportEventListener.handleEvent(evaluationErsReportEvent);
        verify(ersRequestService, never()).saveEvaluationResults(any(ErsEvaluationRequestData.class));
    }
}
