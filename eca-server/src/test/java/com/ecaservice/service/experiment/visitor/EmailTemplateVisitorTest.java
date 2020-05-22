package com.ecaservice.service.experiment.visitor;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.experiment.ExperimentType;
import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.service.experiment.dictionary.TemplateVariablesDictionary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests that checks EmailTemplateVisitor functionality {@see EmailTemplateVisitor}.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import(ExperimentConfig.class)
public class EmailTemplateVisitorTest {

    @Inject
    private ExperimentConfig experimentConfig;

    private EmailTemplateVisitor emailTemplateVisitor;

    @BeforeEach
    void setUp() {
        emailTemplateVisitor = new EmailTemplateVisitor(experimentConfig);
    }

    @Test
    void testNewStatusContext() {
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.TEST_UUID);
        experiment.setRequestStatus(RequestStatus.NEW);
        EmailRequest emailRequest = experiment.getRequestStatus().handle(emailTemplateVisitor, experiment);
        assertEmailRequest(emailRequest, experiment);
    }

    @Test
    void testErrorStatusContext() {
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.TEST_UUID);
        experiment.setRequestStatus(RequestStatus.ERROR);
        EmailRequest emailRequest = experiment.getRequestStatus().handle(emailTemplateVisitor, experiment);
        assertEmailRequest(emailRequest, experiment);
    }

    @Test
    void testTimeoutStatusContext() {
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.TEST_UUID);
        experiment.setRequestStatus(RequestStatus.TIMEOUT);
        EmailRequest emailRequest = experiment.getRequestStatus().handle(emailTemplateVisitor, experiment);
        assertEmailRequest(emailRequest, experiment);
        assertThat(Integer.valueOf(emailRequest.getEmailMessageVariables().get(
                TemplateVariablesDictionary.TIMEOUT_KEY).toString())).isEqualTo(experimentConfig.getTimeout());
    }

    @Test
    void testFinishedStatusContext() {
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.TEST_UUID);
        experiment.setRequestStatus(RequestStatus.FINISHED);
        EmailRequest emailRequest = experiment.getRequestStatus().handle(emailTemplateVisitor, experiment);
        assertEmailRequest(emailRequest, experiment);
        String actualUrl =
                emailRequest.getEmailMessageVariables().get(TemplateVariablesDictionary.DOWNLOAD_URL_KEY).toString();
        assertThat(actualUrl).isNotNull();
    }

    private void assertEmailRequest(EmailRequest emailRequest, Experiment experiment) {
        Map<String, Object> variablesMap = emailRequest.getEmailMessageVariables();
        assertThat(variablesMap.get(TemplateVariablesDictionary.FIRST_NAME_KEY)).isEqualTo(
                experiment.getFirstName());
        ExperimentType actualExperimentType =
                ExperimentType.findByDescription(
                        variablesMap.get(TemplateVariablesDictionary.EXPERIMENT_TYPE_KEY).toString());
        assertThat(actualExperimentType).isEqualTo(experiment.getExperimentType());
        assertThat(variablesMap.get(TemplateVariablesDictionary.REQUEST_ID_KEY).toString()).isEqualTo(
                experiment.getRequestId());
    }
}
