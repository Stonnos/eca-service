package com.ecaservice.server.event.listener;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.server.event.model.EvaluationResponseEvent;
import com.ecaservice.server.mapping.EcaResponseMapper;
import com.ecaservice.server.service.EcaResponseSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.createEvaluationResultsModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ExperimentResponseEventListener} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class EvaluationResponseEventListenerTest {

    private static final String REPLY_TO = "replyTo";

    @Mock
    private EcaResponseSender ecaResponseSender;
    @Mock
    private EcaResponseMapper ecaResponseMapper;

    @Captor
    private ArgumentCaptor<String> correlationIdCaptor;
    @Captor
    private ArgumentCaptor<String> replyToCaptor;

    @InjectMocks
    private EvaluationResponseEventListener evaluationResponseEventListener;


    @Test
    void testHandleExperimentResponse() {
        var evaluationResultsModel = createEvaluationResultsModel(UUID.randomUUID().toString());
        var event =
                new EvaluationResponseEvent(this, evaluationResultsModel, UUID.randomUUID().toString(), REPLY_TO);
        when(ecaResponseMapper.map(evaluationResultsModel)).thenReturn(new EvaluationResponse());
        evaluationResponseEventListener.handleEvaluationResponseEvent(event);
        verify(ecaResponseSender).sendResponse(any(EvaluationResponse.class), correlationIdCaptor.capture(),
                replyToCaptor.capture());
        assertThat(replyToCaptor.getValue()).isEqualTo(event.getReplyTo());
        assertThat(correlationIdCaptor.getValue()).isEqualTo(event.getCorrelationId());
    }
}
