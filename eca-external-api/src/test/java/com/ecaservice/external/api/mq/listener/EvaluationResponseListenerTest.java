package com.ecaservice.external.api.mq.listener;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import com.ecaservice.external.api.repository.ExperimentRequestRepository;
import com.ecaservice.external.api.service.EvaluationResponseHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.util.Optional;

import static com.ecaservice.external.api.TestHelperUtils.buildMessageProperties;
import static com.ecaservice.external.api.TestHelperUtils.createEvaluationRequestEntity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link EvaluationResponseListener} class functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class EvaluationResponseListenerTest {

    @Mock
    private EvaluationResponseHandler evaluationResponseHandler;
    @Mock
    private EcaRequestRepository ecaRequestRepository;
    @Mock
    private ExperimentRequestRepository experimentRequestRepository;
    @Mock
    private EvaluationRequestRepository evaluationRequestRepository;

    @InjectMocks
    private EvaluationResponseListener evaluationResponseListener;

    @Test
    void testHandleMessageWithInvalidCorrelationId() {
        Message message = Mockito.mock(Message.class);
        MessageProperties messageProperties = buildMessageProperties();
        when(message.getMessageProperties()).thenReturn(messageProperties);
        when(evaluationRequestRepository.findByCorrelationId(messageProperties.getCorrelationId())).thenReturn(
                Optional.empty());
        evaluationResponseListener.handleResponseMessage(new EvaluationResponse(), message);
        verify(evaluationResponseHandler, never()).handleResponse(any(EvaluationRequestEntity.class),
                any(EvaluationResponse.class));
    }

    @Test
    void testHandleExceededMessage() {
        Message message = Mockito.mock(Message.class);
        MessageProperties messageProperties = buildMessageProperties();
        when(message.getMessageProperties()).thenReturn(messageProperties);
        EvaluationRequestEntity evaluationRequestEntity =
                createEvaluationRequestEntity(messageProperties.getCorrelationId());
        evaluationRequestEntity.setRequestStage(RequestStageType.EXCEEDED);
        when(evaluationRequestRepository.findByCorrelationId(messageProperties.getCorrelationId())).thenReturn(
                Optional.of(evaluationRequestEntity));
        evaluationResponseListener.handleResponseMessage(new EvaluationResponse(), message);
        verify(evaluationResponseHandler, never()).handleResponse(any(EvaluationRequestEntity.class),
                any(EvaluationResponse.class));
    }

    @Test
    void testHandleMessage() {
        Message message = Mockito.mock(Message.class);
        MessageProperties messageProperties = buildMessageProperties();
        when(message.getMessageProperties()).thenReturn(messageProperties);
        EvaluationRequestEntity evaluationRequestEntity =
                createEvaluationRequestEntity(messageProperties.getCorrelationId());
        evaluationRequestEntity.setRequestStage(RequestStageType.REQUEST_SENT);
        //Mock methods
        when(evaluationRequestRepository.findByCorrelationId(messageProperties.getCorrelationId()))
                .thenReturn(Optional.of(evaluationRequestEntity));
        //Verify that response is being sent to client
        evaluationResponseListener.handleResponseMessage(new EvaluationResponse(), message);
        verify(evaluationResponseHandler, atLeastOnce()).handleResponse(any(EvaluationRequestEntity.class),
                any(EvaluationResponse.class));
    }
}
