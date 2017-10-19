package com.ecaservice.service.experiment.visitor;

import com.ecaservice.TestDataHelper;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentStatus;
import com.ecaservice.model.experiment.ExperimentType;
import com.ecaservice.service.experiment.dictionary.TemplateVariablesDictionary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.context.Context;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
public class EmailTemplateVisitorTest {

    private static final int TIMEOUT_VALUE = 5;
    private static final String DOWNLOAD_URL_FORMAT = "http://localhost/download/%s";

    @Mock
    private ExperimentConfig experimentConfig;

    private EmailTemplateVisitor emailTemplateVisitor;

    @Before
    public void setUp() {
        emailTemplateVisitor = new EmailTemplateVisitor(experimentConfig);
        when(experimentConfig.getTimeout()).thenReturn(TIMEOUT_VALUE);
        when(experimentConfig.getDownloadUrl()).thenReturn(DOWNLOAD_URL_FORMAT);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNewStatusContext() {
        Experiment experiment = new Experiment();
        experiment.setExperimentStatus(ExperimentStatus.NEW);
        experiment.getExperimentStatus().handle(emailTemplateVisitor, experiment);
    }

    @Test
    public void testErrorStatusContext() {
        Experiment experiment = TestDataHelper.createExperiment(null);
        experiment.setExperimentStatus(ExperimentStatus.ERROR);
        Context context = experiment.getExperimentStatus().handle(emailTemplateVisitor, experiment);
        assertContext(context, experiment);
    }

    @Test
    public void testTimeoutStatusContext() {
        Experiment experiment = TestDataHelper.createExperiment(null);
        experiment.setExperimentStatus(ExperimentStatus.TIMEOUT);
        Context context = experiment.getExperimentStatus().handle(emailTemplateVisitor, experiment);
        assertContext(context, experiment);
        assertEquals(experimentConfig.getTimeout(),
                Integer.valueOf(context.getVariable(TemplateVariablesDictionary.TIMEOUT_KEY).toString()));
    }

    @Test
    public void testFinishedStatusContext() {
        Experiment experiment = TestDataHelper.createExperiment(TestDataHelper.UUID);
        experiment.setExperimentStatus(ExperimentStatus.FINISHED);
        Context context = experiment.getExperimentStatus().handle(emailTemplateVisitor, experiment);
        assertContext(context, experiment);
        String actualUrl = context.getVariable(TemplateVariablesDictionary.DOWNLOAD_URL_KEY).toString();
        assertEquals(String.format(DOWNLOAD_URL_FORMAT, experiment.getUuid()), actualUrl);
    }

    private void assertContext(Context context, Experiment experiment) {
        assertEquals(context.getVariable(TemplateVariablesDictionary.FIRST_NAME_KEY),
                experiment.getFirstName());
        ExperimentType actualExperimentType =
                ExperimentType.findByDescription(context.getVariable(TemplateVariablesDictionary.EXPERIMENT_TYPE_KEY).toString());
        assertEquals(actualExperimentType, experiment.getExperimentType());
    }
}
