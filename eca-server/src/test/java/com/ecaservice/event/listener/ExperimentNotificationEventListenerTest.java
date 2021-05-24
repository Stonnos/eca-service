package com.ecaservice.event.listener;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.event.model.ExperimentNotificationEvent;
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
    void testHandleChangeStatusEventForNewRequest() {
        internalTestChangeStatusEvent(RequestStatus.NEW);
        verify(experimentEmailVisitor, atLeastOnce()).caseNew(any(Experiment.class));
    }

    @Test
    void testHandleChangeStatusEventForInProgressRequest() {
        internalTestChangeStatusEvent(RequestStatus.IN_PROGRESS);
        verify(experimentEmailVisitor, atLeastOnce()).caseInProgress(any(Experiment.class));
    }

    @Test
    void testHandleChangeStatusEventForFinishedRequest() {
        internalTestChangeStatusEvent(RequestStatus.FINISHED);
        verify(experimentEmailVisitor, atLeastOnce()).caseFinished(any(Experiment.class));
    }

    @Test
    void testHandleChangeStatusEventFoErrorRequest() {
        internalTestChangeStatusEvent(RequestStatus.ERROR);
        verify(experimentEmailVisitor, atLeastOnce()).caseError(any(Experiment.class));
    }

    @Test
    void testHandleChangeStatusEventForTimeoutRequest() {
        internalTestChangeStatusEvent(RequestStatus.TIMEOUT);
        verify(experimentEmailVisitor, atLeastOnce()).caseTimeout(any(Experiment.class));
    }

    private void internalTestChangeStatusEvent(RequestStatus requestStatus) {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experiment.setRequestStatus(requestStatus);
        ExperimentNotificationEvent
                experimentCreatedEvent = new ExperimentNotificationEvent(this, experiment);
        experimentNotificationEventListener.handleChangeStatusEvent(experimentCreatedEvent);
    }
}
