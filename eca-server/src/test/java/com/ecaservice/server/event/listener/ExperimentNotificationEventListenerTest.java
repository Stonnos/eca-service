package com.ecaservice.server.event.listener;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.event.model.ExperimentEmailEvent;
import com.ecaservice.server.service.experiment.mail.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

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
    private NotificationService notificationService;

    @InjectMocks
    private ExperimentNotificationEventListener experimentNotificationEventListener;

    @Test
    void testHandleEmailEvent() {
        var experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        var experimentEmailEvent = new ExperimentEmailEvent(this, experiment);
        experimentNotificationEventListener.handleExperimentEmailEvent(experimentEmailEvent);
        verify(notificationService, atLeastOnce()).notifyByEmail(experiment);
    }
}
