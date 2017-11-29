package com.ecaservice.service.experiment.visitor;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentStatus;
import com.ecaservice.model.experiment.ExperimentType;
import com.ecaservice.service.experiment.dictionary.TemplateVariablesDictionary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.context.Context;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests that checks EmailTemplateVisitor functionality (see {@link EmailTemplateVisitor}).
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(ExperimentConfig.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application-test.properties")
public class EmailTemplateVisitorTest {

    @Autowired
    private ExperimentConfig experimentConfig;

    private EmailTemplateVisitor emailTemplateVisitor;

    @Before
    public void setUp() {
        emailTemplateVisitor = new EmailTemplateVisitor(experimentConfig);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNewStatusContext() {
        Experiment experiment = new Experiment();
        experiment.setExperimentStatus(ExperimentStatus.NEW);
        experiment.getExperimentStatus().handle(emailTemplateVisitor, experiment);
    }

    @Test
    public void testErrorStatusContext() {
        Experiment experiment = TestHelperUtils.createExperiment(null);
        experiment.setExperimentStatus(ExperimentStatus.ERROR);
        Context context = experiment.getExperimentStatus().handle(emailTemplateVisitor, experiment);
        assertContext(context, experiment);
    }

    @Test
    public void testTimeoutStatusContext() {
        Experiment experiment = TestHelperUtils.createExperiment(null);
        experiment.setExperimentStatus(ExperimentStatus.TIMEOUT);
        Context context = experiment.getExperimentStatus().handle(emailTemplateVisitor, experiment);
        assertContext(context, experiment);
        assertEquals(experimentConfig.getTimeout(),
                Integer.valueOf(context.getVariable(TemplateVariablesDictionary.TIMEOUT_KEY).toString()));
    }

    @Test
    public void testFinishedStatusContext() {
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.UUID);
        experiment.setExperimentStatus(ExperimentStatus.FINISHED);
        Context context = experiment.getExperimentStatus().handle(emailTemplateVisitor, experiment);
        assertContext(context, experiment);
        String actualUrl = context.getVariable(TemplateVariablesDictionary.DOWNLOAD_URL_KEY).toString();
        assertEquals(String.format(experimentConfig.getDownloadUrl(), experiment.getUuid()), actualUrl);
    }

    private void assertContext(Context context, Experiment experiment) {
        assertEquals(context.getVariable(TemplateVariablesDictionary.FIRST_NAME_KEY),
                experiment.getFirstName());
        ExperimentType actualExperimentType =
                ExperimentType.findByDescription(
                        context.getVariable(TemplateVariablesDictionary.EXPERIMENT_TYPE_KEY).toString());
        assertEquals(actualExperimentType, experiment.getExperimentType());
    }
}
