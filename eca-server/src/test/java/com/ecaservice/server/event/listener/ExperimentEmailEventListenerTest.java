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
 * Unit tests for checking {@link ExperimentEmailEventListener} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class ExperimentEmailEventListenerTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private ExperimentEmailEventListener experimentEmailEventListener;

    @BeforeEach
    void init() {
        experimentEmailEventListener =
                new ExperimentEmailEventListener(new ExperimentEmailEventVisitor(), applicationEventPublisher);
    }

    @Test
    void testHandleEmailEvent() {
        var experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        var experimentEmailEvent = new ExperimentEmailEvent(this, experiment);
        experimentEmailEventListener.handleExperimentEmailEvent(experimentEmailEvent);
        verify(applicationEventPublisher, atLeastOnce()).publishEvent(any(AbstractExperimentEmailEvent.class));
    }
}
