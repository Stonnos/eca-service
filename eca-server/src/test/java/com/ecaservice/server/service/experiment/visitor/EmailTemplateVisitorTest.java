package com.ecaservice.server.service.experiment.visitor;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.service.experiment.dictionary.TemplateVariablesDictionary;
import com.ecaservice.server.service.experiment.dictionary.Templates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

import static com.ecaservice.notification.util.Priority.MEDIUM;
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
class EmailTemplateVisitorTest {

    private static final String DOWNLOAD_PATH_FORMAT = "%s/eca-api/experiment/download/%s";

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
        assertThat(emailRequest.getTemplateCode()).isEqualTo(Templates.NEW_EXPERIMENT);
    }

    @Test
    void testInProgressStatusContext() {
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.TEST_UUID);
        experiment.setRequestStatus(RequestStatus.IN_PROGRESS);
        EmailRequest emailRequest = experiment.getRequestStatus().handle(emailTemplateVisitor, experiment);
        assertEmailRequest(emailRequest, experiment);
        assertThat(emailRequest.getTemplateCode()).isEqualTo(Templates.IN_PROGRESS_EXPERIMENT);
    }

    @Test
    void testErrorStatusContext() {
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.TEST_UUID);
        experiment.setRequestStatus(RequestStatus.ERROR);
        EmailRequest emailRequest = experiment.getRequestStatus().handle(emailTemplateVisitor, experiment);
        assertEmailRequest(emailRequest, experiment);
        assertThat(emailRequest.getTemplateCode()).isEqualTo(Templates.ERROR_EXPERIMENT);
    }

    @Test
    void testTimeoutStatusContext() {
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.TEST_UUID);
        experiment.setRequestStatus(RequestStatus.TIMEOUT);
        EmailRequest emailRequest = experiment.getRequestStatus().handle(emailTemplateVisitor, experiment);
        assertEmailRequest(emailRequest, experiment);
        assertThat(emailRequest.getTemplateCode()).isEqualTo(Templates.TIMEOUT_EXPERIMENT);
        assertThat(Integer.valueOf(emailRequest.getVariables().get(
                TemplateVariablesDictionary.TIMEOUT_KEY))).isEqualTo(experimentConfig.getTimeout());
    }

    @Test
    void testFinishedStatusContext() {
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.TEST_UUID);
        experiment.setToken(UUID.randomUUID().toString());
        experiment.setRequestStatus(RequestStatus.FINISHED);
        EmailRequest emailRequest = experiment.getRequestStatus().handle(emailTemplateVisitor, experiment);
        assertEmailRequest(emailRequest, experiment);
        assertThat(emailRequest.getTemplateCode()).isEqualTo(Templates.FINISHED_EXPERIMENT);
        String actualUrl = emailRequest.getVariables().get(TemplateVariablesDictionary.DOWNLOAD_URL_KEY);
        assertThat(actualUrl)
                .isNotNull()
                .isEqualTo(String.format(DOWNLOAD_PATH_FORMAT, experimentConfig.getDownloadBaseUrl(),
                        experiment.getToken()));
    }

    private void assertEmailRequest(EmailRequest emailRequest, Experiment experiment) {
        Map<String, String> variablesMap = emailRequest.getVariables();
        assertThat(variablesMap).containsEntry(TemplateVariablesDictionary.FIRST_NAME_KEY, experiment.getFirstName());
        ExperimentType actualExperimentType =
                ExperimentType.findByDescription(
                        variablesMap.get(TemplateVariablesDictionary.EXPERIMENT_TYPE_KEY));
        assertThat(actualExperimentType).isEqualTo(experiment.getExperimentType());
        assertThat(variablesMap.get(TemplateVariablesDictionary.REQUEST_ID_KEY)).hasToString(experiment.getRequestId());
        assertThat(emailRequest.getPriority()).isEqualTo(MEDIUM);
    }
}
