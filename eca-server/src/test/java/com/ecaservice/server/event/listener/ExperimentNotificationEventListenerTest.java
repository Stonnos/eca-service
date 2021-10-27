package com.ecaservice.server.event.listener;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.event.model.ExperimentEmailEvent;
import com.ecaservice.server.event.model.ExperimentWebPushEvent;
import com.ecaservice.server.service.experiment.mail.NotificationService;
import com.ecaservice.server.service.push.WebPushService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ExperimentNotificationEventListener} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class ExperimentNotificationEventListenerTest {

    @Mock
    private NotificationService notificationService;
    @Mock
    private WebPushService webPushService;

    private ExperimentNotificationEventListener experimentNotificationEventListener;

    @BeforeEach
    void init() {
       AppProperties appProperties = new AppProperties();
       appProperties.getNotifications().setWebPushesEnabled(true);
       experimentNotificationEventListener = new ExperimentNotificationEventListener(appProperties,
               notificationService, webPushService);
    }

    @Test
    void testHandleEmailEvent() {
        var experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        var experimentEmailEvent = new ExperimentEmailEvent(this, experiment);
        experimentNotificationEventListener.handleExperimentEmailEvent(experimentEmailEvent);
        verify(notificationService, atLeastOnce()).notifyByEmail(experiment);
    }

    @Test
    void testHandleWebPushEvent() {
        var experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        var experimentPushEvent = new ExperimentWebPushEvent(this, experiment);
        experimentNotificationEventListener.handleExperimentPushEvent(experimentPushEvent);
        verify(webPushService, atLeastOnce()).sendWebPush(experiment);
    }
}
