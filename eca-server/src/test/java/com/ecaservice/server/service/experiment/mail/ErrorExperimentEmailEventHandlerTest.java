package com.ecaservice.server.service.experiment.mail;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.event.model.mail.ErrorExperimentEmailEvent;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.service.experiment.dictionary.Templates;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ErrorExperimentEmailEventHandler} class.
 *
 * @author Roman Batygin
 */
class ErrorExperimentEmailEventHandlerTest extends AbstractExperimentEmailEventHandlerTest {

    private ErrorExperimentEmailEventHandler errorExperimentEmailEventHandler = new ErrorExperimentEmailEventHandler();

    @Test
    void testHandler() {
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.TEST_UUID, RequestStatus.ERROR);
        EmailRequest emailRequest = errorExperimentEmailEventHandler.handle(new ErrorExperimentEmailEvent(this,
                experiment));
        assertEmailRequest(emailRequest, experiment, Templates.ERROR_EXPERIMENT);
    }
}