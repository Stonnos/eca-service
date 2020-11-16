package com.ecaservice.external.api.mq.listener;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.dto.RequestStatus;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import com.ecaservice.external.api.service.EcaResponseHandler;
import com.ecaservice.external.api.service.MessageCorrelationService;
import com.ecaservice.external.api.service.ResponseBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import reactor.core.publisher.MonoSink;

import java.util.Optional;

import static com.ecaservice.external.api.TestHelperUtils.buildMessageProperties;
import static com.ecaservice.external.api.TestHelperUtils.createEvaluationRequestEntity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link EcaResponseListener} class functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class EcaResponseListenerTest {

    @Mock
    private EcaResponseHandler ecaResponseHandler;
    @Mock
    private ResponseBuilder responseBuilder;
    @Mock
    private MessageCorrelationService messageCorrelationService;
    @Mock
    private EvaluationRequestRepository evaluationRequestRepository;

    @InjectMocks
    private EcaResponseListener ecaResponseListener;

    @Test
    void testHandleMessageWithInvalidCorrelationId() {
        Message message = Mockito.mock(Message.class);
        MessageProperties messageProperties = buildMessageProperties();
        when(message.getMessageProperties()).thenReturn(messageProperties);
        when(evaluationRequestRepository.findByCorrelationId(messageProperties.getCorrelationId())).thenReturn(null);
        ecaResponseListener.handleEvaluationMessage(new EvaluationResponse(), message);
        verify(ecaResponseHandler, never()).handleResponse(any(EvaluationRequestEntity.class),
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
                evaluationRequestEntity);
        ecaResponseListener.handleEvaluationMessage(new EvaluationResponse(), message);
        verify(ecaResponseHandler, never()).handleResponse(any(EvaluationRequestEntity.class),
                any(EvaluationResponse.class));
    }

    @Test
    void testHandleMessage() {
        MonoSink<EvaluationResponseDto> sink = Mockito.mock(MonoSink.class);
        Message message = Mockito.mock(Message.class);
        MessageProperties messageProperties = buildMessageProperties();
        when(message.getMessageProperties()).thenReturn(messageProperties);
        EvaluationRequestEntity evaluationRequestEntity =
                createEvaluationRequestEntity(messageProperties.getCorrelationId());
        evaluationRequestEntity.setRequestStage(RequestStageType.REQUEST_SENT);
        //Mock methods
        when(evaluationRequestRepository.findByCorrelationId(messageProperties.getCorrelationId())).thenReturn(
                evaluationRequestEntity);
        when(messageCorrelationService.pop(messageProperties.getCorrelationId())).thenReturn(Optional.of(sink));
        EvaluationResponseDto evaluationResponseDto = EvaluationResponseDto.builder()
                .requestId(messageProperties.getCorrelationId())
                .status(RequestStatus.SUCCESS)
                .build();
        when(responseBuilder.buildResponse(any(EvaluationResponse.class),
                any(EvaluationRequestEntity.class))).thenReturn(evaluationResponseDto);
        //Verify that response is being sent to client
        ecaResponseListener.handleEvaluationMessage(new EvaluationResponse(), message);
        verify(ecaResponseHandler, atLeastOnce()).handleResponse(any(EvaluationRequestEntity.class),
                any(EvaluationResponse.class));
        verify(sink).success(evaluationResponseDto);
    }
}
