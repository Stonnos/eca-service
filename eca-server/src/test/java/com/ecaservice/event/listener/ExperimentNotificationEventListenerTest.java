package com.ecaservice.event.listener;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.event.model.ExperimentEmailEvent;
import com.ecaservice.event.model.ExperimentWebPushEvent;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.service.experiment.visitor.ExperimentEmailVisitor;
import com.ecaservice.service.push.WebPushService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private ExperimentEmailVisitor experimentEmailVisitor;
    @Mock
    private WebPushService webPushService;

    @InjectMocks
    private ExperimentNotificationEventListener experimentNotificationEventListener;

    @Test
    void testHandleEmailEventForNewRequest() {
        internalTestEmailEvent(RequestStatus.NEW);
        verify(experimentEmailVisitor, atLeastOnce()).caseNew(any(Experiment.class));
    }

    @Test
    void testHandleEmailEventForInProgressRequest() {
        internalTestEmailEvent(RequestStatus.IN_PROGRESS);
        verify(experimentEmailVisitor, atLeastOnce()).caseInProgress(any(Experiment.class));
    }

    @Test
    void testHandleEmailEventForFinishedRequest() {
        internalTestEmailEvent(RequestStatus.FINISHED);
        verify(experimentEmailVisitor, atLeastOnce()).caseFinished(any(Experiment.class));
    }

    @Test
    void testHandleEmailEventFoErrorRequest() {
        internalTestEmailEvent(RequestStatus.ERROR);
        verify(experimentEmailVisitor, atLeastOnce()).caseError(any(Experiment.class));
    }

    @Test
    void testHandleEmailEventForTimeoutRequest() {
        internalTestEmailEvent(RequestStatus.TIMEOUT);
        verify(experimentEmailVisitor, atLeastOnce()).caseTimeout(any(Experiment.class));
    }
    
    @Test
    void testHandleWebPushEvent() {
        var experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        var experimentPushEvent = new ExperimentWebPushEvent(this, experiment);
        experimentNotificationEventListener.handleExperimentPushEvent(experimentPushEvent);
        verify(webPushService, atLeastOnce()).sendWebPush(experiment);
    }

    private void internalTestEmailEvent(RequestStatus requestStatus) {
        var experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experiment.setRequestStatus(requestStatus);
        var experimentEmailEvent = new ExperimentEmailEvent(this, experiment);
        experimentNotificationEventListener.handleExperimentEmailEvent(experimentEmailEvent);
    }
}
