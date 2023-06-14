package com.ecaservice.server.mq.listener;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.event.model.ExperimentEmailEvent;
import com.ecaservice.server.event.model.ExperimentResponseEvent;
import com.ecaservice.server.event.model.push.ExperimentWebPushEvent;
import com.ecaservice.server.model.experiment.AbstractExperimentRequestData;
import com.ecaservice.server.service.experiment.ExperimentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
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
    private ExperimentService experimentService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ExperimentRequestListener experimentRequestListener;

    @Test
    void testHandleMessage() {
        var experimentRequest = TestHelperUtils.createExperimentRequest();
        Message message = Mockito.mock(Message.class);
        when(experimentService.createExperiment(any(AbstractExperimentRequestData.class)))
                .thenReturn(TestHelperUtils.createExperiment(UUID.randomUUID().toString()));
        MessageProperties messageProperties = TestHelperUtils.buildMessageProperties();
        when(message.getMessageProperties()).thenReturn(messageProperties);
        experimentRequestListener.handleMessage(experimentRequest, message);
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ExperimentResponseEvent.class));
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ExperimentEmailEvent.class));
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ExperimentWebPushEvent.class));
    }
}
