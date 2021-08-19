package com.ecaservice.mq.listener;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.InstancesRequest;
import com.ecaservice.event.model.EvaluationFinishedEvent;
import com.ecaservice.service.evaluation.EvaluationOptimizerService;
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
 * Unit tests for checking {@link EvaluationOptimizerRequestListener} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class EvaluationOptimizerRequestListenerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private EvaluationOptimizerService evaluationOptimizerService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private EvaluationOptimizerRequestListener evaluationOptimizerRequestListener;

    @Captor
    private ArgumentCaptor<String> replyToCaptor;

    @Test
    void testHandleMessage() {
        InstancesRequest instancesRequest = new InstancesRequest();
        Message message = Mockito.mock(Message.class);
        when(evaluationOptimizerService.evaluateWithOptimalClassifierOptions(instancesRequest)).thenReturn(
                new EvaluationResponse());
        MessageProperties messageProperties = TestHelperUtils.buildMessageProperties();
        when(message.getMessageProperties()).thenReturn(messageProperties);
        evaluationOptimizerRequestListener.handleMessage(instancesRequest, message);
        verify(eventPublisher, atLeastOnce()).publishEvent(any(EvaluationFinishedEvent.class));
        verify(rabbitTemplate).convertAndSend(replyToCaptor.capture(), any(EvaluationResponse.class),
                any(MessagePostProcessor.class));
        Assertions.assertThat(replyToCaptor.getValue()).isEqualTo(messageProperties.getReplyTo());
    }
}
