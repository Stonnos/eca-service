package com.ecaservice.server.event.listener;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.server.event.model.EvaluationResponseEvent;
import com.ecaservice.server.mapping.EcaResponseMapper;
import com.ecaservice.server.model.evaluation.EvaluationResultsDataModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.createEvaluationResultsDataModel;
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
    private RabbitTemplate rabbitTemplate;
    @Mock
    private EcaResponseMapper ecaResponseMapper;

    @Captor
    private ArgumentCaptor<String> replyToCaptor;

    @InjectMocks
    private EvaluationResponseEventListener evaluationResponseEventListener;


    @Test
    void testHandleExperimentResponse() {
        EvaluationResultsDataModel evaluationResultsDataModel =
                createEvaluationResultsDataModel(UUID.randomUUID().toString());
        var event =
                new EvaluationResponseEvent(this, evaluationResultsDataModel, UUID.randomUUID().toString(), REPLY_TO);
        when(ecaResponseMapper.map(evaluationResultsDataModel)).thenReturn(new EvaluationResponse());
        evaluationResponseEventListener.handleEvaluationResponseEvent(event);
        verify(rabbitTemplate).convertAndSend(replyToCaptor.capture(), any(EvaluationResponse.class),
                any(MessagePostProcessor.class));
        assertThat(replyToCaptor.getValue()).isEqualTo(event.getReplyTo());
    }
}
