package com.ecaservice.server.service.experiment.mail;

import com.ecaservice.core.mail.client.event.model.EmailEvent;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.service.experiment.visitor.EmailTemplateVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Unit tests that checks NotificationService functionality {@see NotificationService}.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({EmailTemplateVisitor.class, ExperimentConfig.class})
class NotificationServiceTest {

    @Inject
    private EmailTemplateVisitor statusTemplateVisitor;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private NotificationService notificationService;

    @BeforeEach
    void init() {
        notificationService = new NotificationService(statusTemplateVisitor, applicationEventPublisher);
    }

    @Test
    void testSuccessNotification() {
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.TEST_UUID);
        notificationService.notifyByEmail(experiment);
        verify(applicationEventPublisher, atLeastOnce()).publishEvent(any(EmailEvent.class));
    }
}
