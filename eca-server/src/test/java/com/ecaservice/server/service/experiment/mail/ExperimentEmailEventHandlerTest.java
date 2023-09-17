package com.ecaservice.server.service.experiment.mail;

import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.event.model.mail.ExperimentEmailEvent;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static com.ecaservice.notification.util.Priority.MEDIUM;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ExperimentEmailEventHandler} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({ExperimentEmailEventHandler.class, ExperimentEmailTemplateVariableVisitorImpl.class, ExperimentConfig.class})
class ExperimentEmailEventHandlerTest {

    private static final String TEMPLATE_CODE = "FINISHED_EXPERIMENT";

    @Inject
    private ExperimentEmailEventHandler experimentEmailEventHandler;

    @Test
    void testHandleEmailEvent() {
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.TEST_UUID, RequestStatus.FINISHED);
        ExperimentEmailEvent emailEvent = new ExperimentEmailEvent(this, experiment, TEMPLATE_CODE,
                List.of(ExperimentEmailTemplateVariable.EXPERIMENT_TYPE, ExperimentEmailTemplateVariable.REQUEST_ID,
                        ExperimentEmailTemplateVariable.DOWNLOAD_URL));
        EmailRequest emailRequest = experimentEmailEventHandler.handle(emailEvent);
        assertEmailRequest(emailRequest, experiment, emailEvent.getTemplateCode());
        String actualUrl =
                emailRequest.getVariables().get(ExperimentEmailTemplateVariable.DOWNLOAD_URL.getVariableName());
        assertThat(actualUrl).isEqualTo(experiment.getExperimentDownloadUrl());
    }

    void assertEmailRequest(EmailRequest emailRequest, Experiment experiment, String expectedTemplateCode) {
        Map<String, String> variablesMap = emailRequest.getVariables();
        ExperimentType actualExperimentType =
                ExperimentType.findByDescription(
                        variablesMap.get(ExperimentEmailTemplateVariable.EXPERIMENT_TYPE.getVariableName()));
        assertThat(actualExperimentType).isEqualTo(experiment.getExperimentType());
        assertThat(variablesMap.get(ExperimentEmailTemplateVariable.REQUEST_ID.getVariableName())).hasToString(
                experiment.getRequestId());
        assertThat(emailRequest.getPriority()).isEqualTo(MEDIUM);
        assertThat(emailRequest.getTemplateCode()).isEqualTo(expectedTemplateCode);
    }
}
