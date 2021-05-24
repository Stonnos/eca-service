package com.ecaservice.listener;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.base.model.EcaResponse;
import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.event.model.ExperimentNotificationEvent;
import com.ecaservice.mapping.EcaResponseMapper;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.service.experiment.ExperimentService;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ExperimentRequestListener} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class ExperimentRequestListenerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private ExperimentService experimentService;
    @Mock
    private EcaResponseMapper ecaResponseMapper;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ExperimentRequestListener experimentRequestListener;

    @Captor
    private ArgumentCaptor<String> replyToCaptor;

    @Test
    void testHandleMessage() {
        ExperimentRequest evaluationRequest = TestHelperUtils.createExperimentRequest();
        Message message = Mockito.mock(Message.class);
        when(experimentService.createExperiment(evaluationRequest)).thenReturn(
                TestHelperUtils.createExperiment(UUID.randomUUID().toString()));
        when(ecaResponseMapper.map(any(Experiment.class))).thenReturn(new EcaResponse());
        MessageProperties messageProperties = TestHelperUtils.buildMessageProperties();
        when(message.getMessageProperties()).thenReturn(messageProperties);
        experimentRequestListener.handleMessage(evaluationRequest, message);
        verify(rabbitTemplate).convertAndSend(replyToCaptor.capture(), any(EcaResponse.class),
                any(MessagePostProcessor.class));
        Assertions.assertThat(replyToCaptor.getValue()).isEqualTo(messageProperties.getReplyTo());
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ExperimentNotificationEvent.class));
    }
}
