package com.ecaservice.service.experiment.mail;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.dto.EmailResponse;
import com.ecaservice.service.experiment.visitor.EmailTemplateVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private EmailClient emailClient;

    private NotificationService notificationService;

    @BeforeEach
    void init() {
        notificationService = new NotificationService(statusTemplateVisitor, emailClient);
    }

    @Test
    void testSuccessNotification() {
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.TEST_UUID);
        when(emailClient.sendEmail(any(EmailRequest.class))).thenReturn(new EmailResponse());
        notificationService.notifyByEmail(experiment);
        verify(emailClient, atLeastOnce()).sendEmail(any(EmailRequest.class));
    }
}
