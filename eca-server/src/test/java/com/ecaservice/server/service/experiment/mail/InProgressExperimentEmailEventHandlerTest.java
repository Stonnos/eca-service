package com.ecaservice.server.service.experiment.mail;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.event.model.mail.InProgressExperimentEmailEvent;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.service.experiment.dictionary.Templates;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link InProgressExperimentEmailEventHandler} class.
 *
 * @author Roman Batygin
 */
class InProgressExperimentEmailEventHandlerTest extends AbstractExperimentEmailEventHandlerTest {

    private InProgressExperimentEmailEventHandler inProgressExperimentEmailEventHandler =
            new InProgressExperimentEmailEventHandler();

    @Test
    void testHandler() {
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.TEST_UUID, RequestStatus.IN_PROGRESS);
        EmailRequest emailRequest =
                inProgressExperimentEmailEventHandler.handle(new InProgressExperimentEmailEvent(this,
                        experiment));
        assertEmailRequest(emailRequest, experiment, Templates.IN_PROGRESS_EXPERIMENT);
    }
}
