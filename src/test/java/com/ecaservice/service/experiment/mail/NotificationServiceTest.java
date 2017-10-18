package com.ecaservice.service.experiment.mail;

import com.ecaservice.TestDataHelper;
import com.ecaservice.config.MailConfig;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentStatus;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.experiment.AbstractExperimentTest;
import com.ecaservice.service.experiment.visitors.EmailTemplateVisitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;

/**
 * Unit tests that checks NotificationService functionality (see {@link NotificationService}).
 * @author Roman Batygin
 */
public class NotificationServiceTest extends AbstractExperimentTest {

    private static final String TEMPLATE_HTML = "template.html";

    @Mock
    private TemplateEngine templateEngine;
    @Mock
    private MailSenderService mailSenderService;
    @Mock
    private MailConfig mailConfig;
    @Mock
    private EmailTemplateVisitor statusTemplateVisitor;
    @Autowired
    private ExperimentRepository experimentRepository;

    private NotificationService notificationService;

    @Before
    public void setUp() {
        notificationService = new NotificationService(templateEngine, mailSenderService,  experimentRepository,
                mailConfig, statusTemplateVisitor);
        HashMap<ExperimentStatus, String> statusMap = new HashMap<>();
        statusMap.put(ExperimentStatus.FINISHED, TEMPLATE_HTML);
        when(mailConfig.getMessageTemplatesMap()).thenReturn(statusMap);
    }

    @After
    public void after() {
        experimentRepository.deleteAll();
    }

    @Ignore
    @Test
    public void testSuccessNotification() {
        Experiment experiment = TestDataHelper.createExperiment(TestDataHelper.UUID);
        experiment.setExperimentStatus(ExperimentStatus.FINISHED);
        experimentRepository.save(experiment);
        when(statusTemplateVisitor.caseFinished(experiment)).thenReturn(new Context());
        notificationService.notifyByEmail(experiment);
        when(templateEngine.process(any(String.class), any(Context.class))).thenReturn("");
        List<Experiment> experimentList = experimentRepository.findAll();
        assertList(experimentList);
        Experiment actualExperiment = experimentList.get(0);
        assertNotNull(actualExperiment.getSentDate());
    }
}
