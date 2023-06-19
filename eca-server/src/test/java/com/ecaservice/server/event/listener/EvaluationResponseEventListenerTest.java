package com.ecaservice.server.event.listener;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.server.event.model.EvaluationResponseEvent;
import com.ecaservice.server.mapping.EcaResponseMapper;
import com.ecaservice.server.mapping.EcaResponseMapperImpl;
import com.ecaservice.server.model.evaluation.EvaluationResultsDataModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.createEvaluationResponseDataModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for checking {@link ExperimentResponseEventListener} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(EcaResponseMapperImpl.class)
class EvaluationResponseEventListenerTest {

    private static final String REPLY_TO = "replyTo";

    @Mock
    private RabbitTemplate rabbitTemplate;
    @Inject
    private EcaResponseMapper ecaResponseMapper;

    @Captor
    private ArgumentCaptor<String> replyToCaptor;
    @Captor
    private ArgumentCaptor<EvaluationResponse> evaluationResponseEventArgumentCaptor;

    private EvaluationResponseEventListener evaluationResponseEventListener;

    @BeforeEach
    void init() {
        evaluationResponseEventListener = new EvaluationResponseEventListener(rabbitTemplate, ecaResponseMapper);
    }

    @Test
    void testHandleExperimentResponse() {
        EvaluationResultsDataModel evaluationResultsDataModel =
                createEvaluationResponseDataModel(UUID.randomUUID().toString());
        var event =
                new EvaluationResponseEvent(this, evaluationResultsDataModel, UUID.randomUUID().toString(), REPLY_TO);
        evaluationResponseEventListener.handleEvaluationResponseEvent(event);
        verify(rabbitTemplate).convertAndSend(replyToCaptor.capture(), evaluationResponseEventArgumentCaptor.capture(),
                any(MessagePostProcessor.class));
        assertThat(replyToCaptor.getValue()).isEqualTo(event.getReplyTo());
        EvaluationResponse actualResponse = evaluationResponseEventArgumentCaptor.getValue();
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getRequestId()).isEqualTo(evaluationResultsDataModel.getRequestId());
        assertThat(actualResponse.getStatus()).isEqualTo(TechnicalStatus.SUCCESS);
    }
}
