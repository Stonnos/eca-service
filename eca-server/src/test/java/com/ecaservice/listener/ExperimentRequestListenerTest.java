package com.ecaservice.listener;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.base.model.EcaResponse;
import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.exception.experiment.ExperimentException;
import com.ecaservice.mapping.EcaResponseMapper;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.service.experiment.ExperimentRequestService;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
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
    private ExperimentRequestService experimentRequestService;
    @Mock
    private EcaResponseMapper ecaResponseMapper;

    @InjectMocks
    private ExperimentRequestListener experimentRequestListener;

    @Captor
    private ArgumentCaptor<String> replyToCaptor;
    @Captor
    private ArgumentCaptor<EcaResponse> ecaResponseArgumentCaptor;

    @Test
    void testHandleMessage() {
        ExperimentRequest evaluationRequest = TestHelperUtils.createExperimentRequest();
        Message message = Mockito.mock(Message.class);
        when(experimentRequestService.createExperimentRequest(evaluationRequest)).thenReturn(
                TestHelperUtils.createExperiment(UUID.randomUUID().toString()));
        when(ecaResponseMapper.map(any(Experiment.class))).thenReturn(new EcaResponse());
        MessageProperties messageProperties = TestHelperUtils.buildMessageProperties();
        when(message.getMessageProperties()).thenReturn(messageProperties);
        experimentRequestListener.handleMessage(evaluationRequest, message);
        verify(rabbitTemplate).convertAndSend(replyToCaptor.capture(), any(EcaResponse.class),
                any(MessagePostProcessor.class));
        Assertions.assertThat(replyToCaptor.getValue()).isEqualTo(messageProperties.getReplyTo());
    }

    @Test
    void testHandleErrorMessage() {
        ExperimentRequest evaluationRequest = TestHelperUtils.createExperimentRequest();
        Message message = Mockito.mock(Message.class);
        when(experimentRequestService.createExperimentRequest(evaluationRequest)).thenThrow(
                new ExperimentException("error"));
        MessageProperties messageProperties = TestHelperUtils.buildMessageProperties();
        when(message.getMessageProperties()).thenReturn(messageProperties);
        experimentRequestListener.handleMessage(evaluationRequest, message);
        verify(rabbitTemplate).convertAndSend(replyToCaptor.capture(), ecaResponseArgumentCaptor.capture(),
                any(MessagePostProcessor.class));
        Assertions.assertThat(replyToCaptor.getValue()).isEqualTo(messageProperties.getReplyTo());
        Assertions.assertThat(ecaResponseArgumentCaptor.getValue()).isNotNull();
        Assertions.assertThat(ecaResponseArgumentCaptor.getValue().getStatus()).isEqualTo(TechnicalStatus.ERROR);
    }
}
