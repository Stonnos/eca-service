package com.ecaservice.server.service.experiment.mail;

import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.service.experiment.dictionary.TemplateVariablesDictionary;

import java.util.Map;

import static com.ecaservice.notification.util.Priority.MEDIUM;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Abstract experiment email event handler test class.
 *
 * @author Roman Batygin
 */
class AbstractExperimentEmailEventHandlerTest {

    void assertEmailRequest(EmailRequest emailRequest, Experiment experiment, String expectedTemplateCode) {
        Map<String, String> variablesMap = emailRequest.getVariables();
        ExperimentType actualExperimentType =
                ExperimentType.findByDescription(
                        variablesMap.get(TemplateVariablesDictionary.EXPERIMENT_TYPE_KEY));
        assertThat(actualExperimentType).isEqualTo(experiment.getExperimentType());
        assertThat(variablesMap.get(TemplateVariablesDictionary.REQUEST_ID_KEY)).hasToString(experiment.getRequestId());
        assertThat(emailRequest.getPriority()).isEqualTo(MEDIUM);
        assertThat(emailRequest.getTemplateCode()).isEqualTo(expectedTemplateCode);
    }
}
