package com.ecaservice.server.service.experiment.step;

import com.ecaservice.s3.client.minio.exception.ObjectStorageException;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.model.entity.ExperimentStepStatus;
import com.ecaservice.server.service.experiment.ExperimentStepService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import static com.ecaservice.server.TestHelperUtils.createExperimentHistory;
import static com.ecaservice.server.TestHelperUtils.loadInstances;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ExperimentErsReportProcessorStepHandler} class.
 *
 * @author Roman Batygin
 */
@Import({ExperimentConfig.class, ExperimentStepService.class, ExperimentErsReportProcessorStepHandler.class})
class ExperimentErsReportProcessorStepHandlerTest extends AbstractStepHandlerTest {

    @MockBean
    private ObjectStorageService objectStorageService;
    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;

    @Inject
    private ExperimentErsReportProcessorStepHandler experimentErsReportProcessorStepHandler;

    @Test
    void testSentErsReportWithError() throws Exception {
        when(objectStorageService.getObject(anyString(), any())).thenThrow(new RuntimeException());
        testStep(experimentErsReportProcessorStepHandler::handle, ExperimentStepStatus.ERROR);
    }

    @Test
    void testSentErsReportFailed() throws Exception {
        when(objectStorageService.getObject(anyString(), any())).thenThrow(
                new ObjectStorageException(new RuntimeException()));
        testStep(experimentErsReportProcessorStepHandler::handle, ExperimentStepStatus.FAILED);
    }

    @Test
    void testSentErsReportSuccess() throws Exception {
        var data = loadInstances();
        var experimentHistory = createExperimentHistory(data);
        when(objectStorageService.getObject(anyString(), any())).thenReturn(experimentHistory);
        testStep(experimentErsReportProcessorStepHandler::handle, ExperimentStepStatus.COMPLETED);
    }
}
