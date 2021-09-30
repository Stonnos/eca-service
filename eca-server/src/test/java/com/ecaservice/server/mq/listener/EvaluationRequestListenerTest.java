package com.ecaservice.server.mq.listener;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.server.event.model.EvaluationFinishedEvent;
import com.ecaservice.server.service.evaluation.EvaluationRequestService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link EvaluationRequestListener} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class EvaluationRequestListenerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private EvaluationRequestService evaluationRequestService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private EvaluationRequestListener evaluationRequestListener;

    @Captor
    private ArgumentCaptor<String> replyToCaptor;

    @Test
    void testHandleMessage() {
        EvaluationRequest evaluationRequest = TestHelperUtils.createEvaluationRequest();
        Message message = Mockito.mock(Message.class);
        when(evaluationRequestService.processRequest(evaluationRequest)).thenReturn(new EvaluationResponse());
        MessageProperties messageProperties = TestHelperUtils.buildMessageProperties();
        when(message.getMessageProperties()).thenReturn(messageProperties);
        evaluationRequestListener.handleMessage(evaluationRequest, message);
        verify(eventPublisher, atLeastOnce()).publishEvent(any(EvaluationFinishedEvent.class));
        verify(rabbitTemplate).convertAndSend(replyToCaptor.capture(), any(EvaluationResponse.class),
                any(MessagePostProcessor.class));
        Assertions.assertThat(replyToCaptor.getValue()).isEqualTo(messageProperties.getReplyTo());
    }
}
