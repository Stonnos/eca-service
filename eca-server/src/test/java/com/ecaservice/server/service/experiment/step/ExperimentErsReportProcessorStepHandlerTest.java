package com.ecaservice.server.service.experiment.step;

import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.model.entity.ExperimentStepStatus;
import com.ecaservice.server.service.experiment.ExperimentStepService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import static com.ecaservice.server.TestHelperUtils.loadInstances;

/**
 * Unit tests for {@link ExperimentErsReportProcessorStepHandler} class.
 *
 * @author Roman Batygin
 */
@Import({ExperimentConfig.class, ExperimentStepService.class, ExperimentErsReportProcessorStepHandler.class})
class ExperimentErsReportProcessorStepHandlerTest extends AbstractStepHandlerTest {

    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;

    @Inject
    private ExperimentErsReportProcessorStepHandler experimentErsReportProcessorStepHandler;

    @Test
    void testSentErsReportSuccess() throws Exception {
        var data = loadInstances();
        testStep(experimentErsReportProcessorStepHandler::handle, ExperimentStepStatus.COMPLETED);
    }
}
