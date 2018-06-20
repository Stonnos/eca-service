package com.ecaservice.service.experiment.mail;

import com.ecaservice.AssertionUtils;
import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.MailConfig;
import com.ecaservice.model.entity.Email;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentStatus;
import com.ecaservice.repository.EmailRepository;
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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.inject.Inject;
import java.util.EnumMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
    private static final int MAX_FAILED_ATTEMPTS_TO_SENT = 10;

    @Mock
    private MailSenderService mailSenderService;
    @Mock
    private MailConfig mailConfig;
    @Mock
    private EmailTemplateVisitor statusTemplateVisitor;
    @Inject
    private ExperimentRepository experimentRepository;
    @Inject
    private EmailRepository emailRepository;

    private TemplateEngine templateEngine;

    private NotificationService notificationService;

    @Before
    public void setUp() {
        emailRepository.deleteAll();
        experimentRepository.deleteAll();
        templateEngine = PowerMockito.mock(TemplateEngine.class);
        notificationService = new NotificationService(templateEngine, mailSenderService, experimentRepository,
                mailConfig, statusTemplateVisitor, emailRepository);
        EnumMap<ExperimentStatus, String> statusMap = new EnumMap<>(ExperimentStatus.class);
        statusMap.put(ExperimentStatus.FINISHED, TEMPLATE_HTML);
        when(mailConfig.getMessageTemplatesMap()).thenReturn(statusMap);
        when(mailConfig.getMaxFailedAttemptsToSent()).thenReturn(MAX_FAILED_ATTEMPTS_TO_SENT);
    }

    @Test
    public void testSuccessNotification() throws Exception {
        Experiment experiment = createAndSaveExperiment();
        when(statusTemplateVisitor.caseFinished(experiment)).thenReturn(new Context());
        when(templateEngine.process(any(String.class), any(Context.class))).thenReturn("message");
        doNothing().when(mailSenderService).sendEmail(any(Email.class));
        notificationService.sendExperimentResults(experiment);
        List<Experiment> experimentList = experimentRepository.findAll();
        AssertionUtils.assertSingletonList(experimentList);
        Experiment actualExperiment = experimentList.get(0);
        assertThat(actualExperiment.getSentDate()).isNotNull();
        AssertionUtils.assertSingletonList(emailRepository.findAll());
    }

    @Test
    public void testErrorNotification() throws Exception {
        Experiment experiment = createAndSaveExperiment();
        when(statusTemplateVisitor.caseFinished(experiment)).thenReturn(new Context());
        when(templateEngine.process(any(String.class), any(Context.class))).thenReturn("message");
        doThrow(new RuntimeException()).when(mailSenderService).sendEmail(any(Email.class));
        int expectedFailedAttemptsToSent = experiment.getFailedAttemptsToSent() + 1;
        notificationService.sendExperimentResults(experiment);
        List<Experiment> experimentList = experimentRepository.findAll();
        AssertionUtils.assertSingletonList(experimentList);
        Experiment actualExperiment = experimentList.get(0);
        assertThat(actualExperiment.getSentDate()).isNull();
        assertThat(actualExperiment.getFailedAttemptsToSent()).isEqualTo(expectedFailedAttemptsToSent);
    }

    @Test
    public void testExceededNotification() throws Exception {
        Experiment experiment = createAndSaveExperiment();
        experiment.setFailedAttemptsToSent(MAX_FAILED_ATTEMPTS_TO_SENT);
        when(statusTemplateVisitor.caseFinished(experiment)).thenReturn(new Context());
        when(templateEngine.process(any(String.class), any(Context.class))).thenReturn("message");
        doThrow(new RuntimeException()).when(mailSenderService).sendEmail(any(Email.class));
        notificationService.sendExperimentResults(experiment);
        List<Experiment> experimentList = experimentRepository.findAll();
        AssertionUtils.assertSingletonList(experimentList);
        Experiment actualExperiment = experimentList.get(0);
        assertThat(actualExperiment.getSentDate()).isNull();
        assertThat(actualExperiment.getExperimentStatus()).isEqualTo(ExperimentStatus.EXCEEDED);
    }

    private Experiment createAndSaveExperiment() {
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.UUID);
        experiment.setExperimentStatus(ExperimentStatus.FINISHED);
        experimentRepository.save(experiment);
        return experiment;
    }
}
