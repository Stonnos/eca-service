package com.ecaservice.event.listener;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.event.model.ExperimentCreatedEvent;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.service.experiment.mail.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for checking {@link ExperimentCreatedEventListener} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class ExperimentCreatedEventListenerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ExperimentCreatedEventListener experimentCreatedEventListener;

    @Test
    void testHandleExperimentCreatedEvent() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        ExperimentCreatedEvent experimentCreatedEvent = new ExperimentCreatedEvent(this, experiment);
        experimentCreatedEventListener.handleExperimentCreatedEvent(experimentCreatedEvent);
        verify(notificationService, atLeastOnce()).notifyByEmail(experiment);
    }
}
