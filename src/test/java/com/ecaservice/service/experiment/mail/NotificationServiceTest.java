package com.ecaservice.service.experiment.mail;

import com.ecaservice.AssertionUtils;
import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.MailConfig;
import com.ecaservice.model.Mail;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentStatus;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.experiment.AbstractExperimentTest;
import com.ecaservice.service.experiment.visitor.EmailTemplateVisitor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.EnumMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Unit tests that checks NotificationService functionality (see {@link NotificationService}).
 *
 * @author Roman Batygin
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest(TemplateEngine.class)
public class NotificationServiceTest extends AbstractExperimentTest {

    private static final String TEMPLATE_HTML = "test-template.html";
    private static final int MAX_FAILED_ATTEMPTS_TO_SENT = 10;

    @Mock
    private MailSenderService mailSenderService;
    @Mock
    private MailConfig mailConfig;
    @Mock
    private EmailTemplateVisitor statusTemplateVisitor;
    @Autowired
    private ExperimentRepository experimentRepository;

    private TemplateEngine templateEngine;

    private NotificationService notificationService;

    @Before
    public void setUp() {
        experimentRepository.deleteAll();
        templateEngine = PowerMockito.mock(TemplateEngine.class);
        notificationService = new NotificationService(templateEngine, mailSenderService, experimentRepository,
                mailConfig, statusTemplateVisitor);
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
        doNothing().when(mailSenderService).sendEmail(any(Mail.class));
        notificationService.notifyByEmail(experiment);
        List<Experiment> experimentList = experimentRepository.findAll();
        AssertionUtils.assertList(experimentList);
        Experiment actualExperiment = experimentList.get(0);
        assertNotNull(actualExperiment.getSentDate());
    }

    @Test
    public void testErrorNotification() throws Exception {
        Experiment experiment = createAndSaveExperiment();
        when(statusTemplateVisitor.caseFinished(experiment)).thenReturn(new Context());
        when(templateEngine.process(any(String.class), any(Context.class)))
                .thenThrow(Exception.class);
        int expectedFailedAttemptsToSent = experiment.getFailedAttemptsToSent() + 1;
        notificationService.notifyByEmail(experiment);
        List<Experiment> experimentList = experimentRepository.findAll();
        AssertionUtils.assertList(experimentList);
        Experiment actualExperiment = experimentList.get(0);
        assertNull(actualExperiment.getSentDate());
        assertEquals(expectedFailedAttemptsToSent, actualExperiment.getFailedAttemptsToSent());
    }

    @Test
    public void testExceededNotification() throws Exception {
        Experiment experiment = createAndSaveExperiment();
        experiment.setFailedAttemptsToSent(MAX_FAILED_ATTEMPTS_TO_SENT);
        when(statusTemplateVisitor.caseFinished(experiment)).thenReturn(new Context());
        when(templateEngine.process(any(String.class), any(Context.class)))
                .thenThrow(Exception.class);
        notificationService.notifyByEmail(experiment);
        List<Experiment> experimentList = experimentRepository.findAll();
        AssertionUtils.assertList(experimentList);
        Experiment actualExperiment = experimentList.get(0);
        assertNull(actualExperiment.getSentDate());
        assertEquals(ExperimentStatus.EXCEEDED, actualExperiment.getExperimentStatus());
    }

    private Experiment createAndSaveExperiment() {
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.UUID);
        experiment.setExperimentStatus(ExperimentStatus.FINISHED);
        experimentRepository.save(experiment);
        return experiment;
    }
}
