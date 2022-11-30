package com.ecaservice.server.service.experiment.mail;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.event.model.mail.FinishedExperimentEmailEvent;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.service.experiment.dictionary.TemplateVariablesDictionary;
import com.ecaservice.server.service.experiment.dictionary.Templates;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link FinishedExperimentEmailEventHandler} class.
 *
 * @author Roman Batygin
 */
class FinishedExperimentEmailEventHandlerTest extends AbstractExperimentEmailEventHandlerTest {

    private FinishedExperimentEmailEventHandler finishedExperimentEmailEventHandler =
            new FinishedExperimentEmailEventHandler();

    @Test
    void testHandler() {
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.TEST_UUID, RequestStatus.FINISHED);
        EmailRequest emailRequest = finishedExperimentEmailEventHandler.handle(new FinishedExperimentEmailEvent(this,
                experiment));
        assertEmailRequest(emailRequest, experiment, Templates.FINISHED_EXPERIMENT);
        String actualUrl = emailRequest.getVariables().get(TemplateVariablesDictionary.DOWNLOAD_URL_KEY);
        assertThat(actualUrl).isEqualTo(experiment.getExperimentDownloadUrl());
    }
}
