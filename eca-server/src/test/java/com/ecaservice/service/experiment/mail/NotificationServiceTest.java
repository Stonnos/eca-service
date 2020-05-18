package com.ecaservice.service.experiment.mail;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.MailConfig;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.dto.EmailResponse;
import com.ecaservice.service.experiment.mail.template.TemplateEngineService;
import com.ecaservice.service.experiment.visitor.EmailTemplateVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.context.Context;

import java.util.EnumMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests that checks NotificationService functionality {@see NotificationService}.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
public class NotificationServiceTest {

    private static final String TEMPLATE_HTML = "test-template.html";
    private static final String WEB_SERVICE_URL = "http://localhost";

    @Mock
    private MailConfig mailConfig;
    @Mock
    private EmailTemplateVisitor statusTemplateVisitor;
    @Mock
    private EmailClient emailClient;
    @Mock
    private TemplateEngineService templateEngineService;
    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    public void init() {
        EnumMap<RequestStatus, String> statusMap = new EnumMap<>(RequestStatus.class);
        statusMap.put(RequestStatus.FINISHED, TEMPLATE_HTML);
        when(mailConfig.getMessageTemplatesMap()).thenReturn(statusMap);
        when(mailConfig.getEnabled()).thenReturn(true);
        when(mailConfig.getServiceUrl()).thenReturn(WEB_SERVICE_URL);
    }

    @Test
    public void testNotificationDisabled() {
        when(mailConfig.getEnabled()).thenReturn(false);
        Experiment experiment =  TestHelperUtils.createExperiment(TestHelperUtils.TEST_UUID);
        notificationService.notifyByEmail(experiment);
        verify(emailClient, never()).sendEmail(any(EmailRequest.class));
    }

    @Test
    public void testSuccessNotification() {
        Experiment experiment =  TestHelperUtils.createExperiment(TestHelperUtils.TEST_UUID);
        when(statusTemplateVisitor.caseFinished(experiment)).thenReturn(new Context());
        when(emailClient.sendEmail(any(EmailRequest.class))).thenReturn(new EmailResponse());
        when(templateEngineService.process(anyString(), any(Context.class))).thenReturn("message");
        notificationService.notifyByEmail(experiment);
        verify(emailClient, atLeastOnce()).sendEmail(any(EmailRequest.class));
    }

}
