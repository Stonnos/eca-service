package com.ecaservice.service.experiment.mail;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.MailConfig;
import com.ecaservice.dto.mail.EmailRequest;
import com.ecaservice.dto.mail.EmailResponse;
import com.ecaservice.dto.mail.ResponseStatus;
import com.ecaservice.exception.NotificationException;
import com.ecaservice.model.entity.EmailRequestEntity;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentStatus;
import com.ecaservice.repository.EmailRequestRepository;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.experiment.visitor.EmailTemplateVisitor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.inject.Inject;
import java.util.EnumMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests that checks NotificationService functionality {@see NotificationService}.
 *
 * @author Roman Batygin
 */
@AutoConfigureDataJpa
@EnableJpaRepositories(basePackageClasses = ExperimentRepository.class)
@EntityScan(basePackageClasses = Experiment.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest(TemplateEngine.class)
public class NotificationServiceTest {

    private static final String TEMPLATE_HTML = "test-template.html";

    @Mock
    private MailConfig mailConfig;
    @Mock
    private EmailTemplateVisitor statusTemplateVisitor;
    @Inject
    private ExperimentRepository experimentRepository;
    @Inject
    private EmailRequestRepository emailRequestRepository;
    @Mock
    private WebServiceTemplate notificationWebServiceTemplate;

    private TemplateEngine templateEngine;

    private NotificationService notificationService;

    @Before
    public void setUp() {
        emailRequestRepository.deleteAll();
        experimentRepository.deleteAll();
        templateEngine = PowerMockito.mock(TemplateEngine.class);
        notificationService = new NotificationService(templateEngine, mailConfig, statusTemplateVisitor,
                notificationWebServiceTemplate, emailRequestRepository);
        EnumMap<ExperimentStatus, String> statusMap = new EnumMap<>(ExperimentStatus.class);
        statusMap.put(ExperimentStatus.FINISHED, TEMPLATE_HTML);
        when(mailConfig.getMessageTemplatesMap()).thenReturn(statusMap);
    }

    @Test
    public void testSuccessNotification() {
        Experiment experiment = createAndSaveExperiment();
        EmailResponse emailResponse = new EmailResponse();
        emailResponse.setStatus(ResponseStatus.SUCCESS);
        emailResponse.setRequestId(UUID.randomUUID().toString());
        when(statusTemplateVisitor.caseFinished(experiment)).thenReturn(new Context());
        when(templateEngine.process(any(String.class), any(Context.class))).thenReturn("message");
        when(notificationWebServiceTemplate.marshalSendAndReceive(any(String.class),
                any(EmailRequest.class))).thenReturn(
                emailResponse);
        notificationService.notifyByEmail(experiment);
        EmailRequestEntity emailRequest = emailRequestRepository.findAll().stream().findFirst().orElse(null);
        assertThat(emailRequest).isNotNull();
        assertThat(emailRequest.getRequestDate()).isNotNull();
        assertThat(emailRequest.getRequestId()).isEqualTo(emailResponse.getRequestId());
        assertThat(emailRequest.getResponseStatus()).isEqualTo(emailResponse.getStatus());
    }

    @Test(expected = NotificationException.class)
    public void testErrorNotification() {
        Experiment experiment = createAndSaveExperiment();
        when(statusTemplateVisitor.caseFinished(experiment)).thenReturn(new Context());
        when(templateEngine.process(any(String.class), any(Context.class))).thenReturn("message");
        when(notificationWebServiceTemplate.marshalSendAndReceive(any(String.class),
                any(EmailRequest.class))).thenThrow(
                new WebServiceIOException("I/O"));
        notificationService.notifyByEmail(experiment);
    }

    private Experiment createAndSaveExperiment() {
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.UUID);
        experiment.setExperimentStatus(ExperimentStatus.FINISHED);
        experimentRepository.save(experiment);
        return experiment;
    }
}
