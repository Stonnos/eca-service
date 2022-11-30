package com.ecaservice.server.event.listener;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.event.model.ExperimentEmailEvent;
import com.ecaservice.server.event.model.mail.AbstractExperimentEmailEvent;
import com.ecaservice.server.service.experiment.visitor.ExperimentEmailEventVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for checking {@link ExperimentNotificationEventListener} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class ExperimentNotificationEventListenerTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private ExperimentNotificationEventListener experimentNotificationEventListener;

    @BeforeEach
    void init() {
        experimentNotificationEventListener =
                new ExperimentNotificationEventListener(new ExperimentEmailEventVisitor(), applicationEventPublisher);
    }

    @Test
    void testHandleEmailEvent() {
        var experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        var experimentEmailEvent = new ExperimentEmailEvent(this, experiment);
        experimentNotificationEventListener.handleExperimentEmailEvent(experimentEmailEvent);
        verify(applicationEventPublisher, atLeastOnce()).publishEvent(any(AbstractExperimentEmailEvent.class));
    }
}
