package com.ecaservice.server.service.experiment.mail;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.event.model.mail.TimeoutExperimentEmailEvent;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.service.experiment.dictionary.TemplateVariablesDictionary;
import com.ecaservice.server.service.experiment.dictionary.Templates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link TimeoutExperimentEmailEventHandler} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({ExperimentConfig.class, TimeoutExperimentEmailEventHandler.class})
class TimeoutExperimentEmailEventHandlerTest extends AbstractExperimentEmailEventHandlerTest {

    @Inject
    private ExperimentConfig experimentConfig;

    @Inject
    private TimeoutExperimentEmailEventHandler timeoutExperimentEmailEventHandler;

    @Test
    void testHandler() {
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.TEST_UUID, RequestStatus.TIMEOUT);
        EmailRequest emailRequest = timeoutExperimentEmailEventHandler.handle(new TimeoutExperimentEmailEvent(this,
                experiment));
        assertEmailRequest(emailRequest, experiment, Templates.TIMEOUT_EXPERIMENT);
        assertThat(Integer.valueOf(emailRequest.getVariables().get(
                TemplateVariablesDictionary.TIMEOUT_KEY))).isEqualTo(experimentConfig.getTimeout());
    }
}
