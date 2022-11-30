package com.ecaservice.server.service.experiment.mail;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.event.model.mail.NewExperimentEmailEvent;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.service.experiment.dictionary.Templates;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link NewExperimentEmailEventHandler} class.
 *
 * @author Roman Batygin
 */
class NewExperimentEmailEventHandlerTest extends AbstractExperimentEmailEventHandlerTest {

    private NewExperimentEmailEventHandler newExperimentEmailEventHandler = new NewExperimentEmailEventHandler();

    @Test
    void testHandler() {
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.TEST_UUID, RequestStatus.NEW);
        EmailRequest emailRequest = newExperimentEmailEventHandler.handle(new NewExperimentEmailEvent(this,
                experiment));
        assertEmailRequest(emailRequest, experiment, Templates.NEW_EXPERIMENT);
    }
}
