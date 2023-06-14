package com.ecaservice.server.mq.listener;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.event.model.ExperimentEmailEvent;
import com.ecaservice.server.event.model.ExperimentResponseEvent;
import com.ecaservice.server.event.model.push.ExperimentSystemPushEvent;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.ExperimentMapper;
import com.ecaservice.server.mapping.ExperimentMapperImpl;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.experiment.AbstractExperimentRequestData;
import com.ecaservice.server.service.experiment.ExperimentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
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
@ExtendWith(SpringExtension.class)
@Import({ExperimentMapperImpl.class, DateTimeConverter.class, InstancesInfoMapperImpl.class})
class ExperimentRequestListenerTest {

    @MockBean
    private ExperimentService experimentService;
    @MockBean
    private ApplicationEventPublisher eventPublisher;

    @Inject
    private ExperimentMapper experimentMapper;

    private ExperimentRequestListener experimentRequestListener;

    @BeforeEach
    void init() {
        experimentRequestListener = new ExperimentRequestListener(experimentService, experimentMapper, eventPublisher);
    }

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
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ExperimentSystemPushEvent.class));
    }
}
