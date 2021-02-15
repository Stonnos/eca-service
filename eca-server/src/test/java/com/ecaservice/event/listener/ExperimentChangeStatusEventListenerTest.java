package com.ecaservice.event.listener;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.event.model.ExperimentChangeStatusEvent;
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
 * Unit tests for checking {@link ExperimentChangeStatusEventListener} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class ExperimentChangeStatusEventListenerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ExperimentChangeStatusEventListener experimentChangeStatusEventListener;

    @Test
    void testHandleChangeStatusEvent() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        ExperimentChangeStatusEvent experimentCreatedEvent = new ExperimentChangeStatusEvent(this, experiment);
        experimentChangeStatusEventListener.handleChangeStatusEvent(experimentCreatedEvent);
        verify(notificationService, atLeastOnce()).notifyByEmail(experiment);
    }
}
