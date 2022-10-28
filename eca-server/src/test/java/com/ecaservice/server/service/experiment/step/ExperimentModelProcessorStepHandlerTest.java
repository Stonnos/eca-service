package com.ecaservice.server.service.experiment.step;

import com.ecaservice.s3.client.minio.exception.ObjectStorageException;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentStepStatus;
import com.ecaservice.server.model.experiment.InitializationParams;
import com.ecaservice.server.service.evaluation.CalculationExecutorService;
import com.ecaservice.server.service.evaluation.CalculationExecutorServiceImpl;
import com.ecaservice.server.service.experiment.ExperimentProcessorService;
import com.ecaservice.server.service.experiment.ExperimentStepService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;
import weka.core.Instances;

import javax.inject.Inject;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.ecaservice.server.TestHelperUtils.createExperimentHistory;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ExperimentModelProcessorStepHandler} class.
 *
 * @author Roman Batygin
 */
@Import({ExperimentConfig.class, ExperimentStepService.class})
class ExperimentModelProcessorStepHandlerTest extends AbstractStepHandlerTest {

    @Mock
    private ObjectStorageService objectStorageService;
    @Mock
    private ExperimentProcessorService experimentProcessorService;

    @Inject
    private ExperimentConfig experimentConfig;
    @Inject
    private ExperimentStepService experimentStepService;

    private ExperimentModelProcessorStepHandler experimentModelProcessorStepHandler;

    private Instances data;

    @Override
    public void init() {
        super.init();
        data = TestHelperUtils.loadInstances();
        var executorService = new CalculationExecutorServiceImpl(Executors.newCachedThreadPool());
        experimentModelProcessorStepHandler = new ExperimentModelProcessorStepHandler(experimentConfig,
                objectStorageService, experimentProcessorService, executorService, experimentStepService);
    }

    @Test
    void testProcessExperimentWithSuccessStatus() throws Exception {
        when(objectStorageService.getObject(anyString(), any())).thenReturn(data);
        var experimentHistory = createExperimentHistory(data);
        when(experimentProcessorService.processExperimentHistory(any(Experiment.class),
                any(InitializationParams.class))).thenReturn(experimentHistory);
        testStep(experimentModelProcessorStepHandler::handle, ExperimentStepStatus.COMPLETED);
    }

    @Test
    void testProcessExperimentWithErrorWhileGetInstances() throws Exception {
        when(objectStorageService.getObject(anyString(), any())).thenThrow(new RuntimeException());
        testStep(experimentModelProcessorStepHandler::handle, ExperimentStepStatus.ERROR);
    }

    @Test
    void testProcessExperimentFailedWhileGetInstances() throws Exception {
        when(objectStorageService.getObject(anyString(), any())).thenThrow(
                new ObjectStorageException(new RuntimeException()));
        testStep(experimentModelProcessorStepHandler::handle, ExperimentStepStatus.FAILED);
    }

    @Test
    void testProcessExperimentWithTimeoutStatus() throws Exception {
        when(objectStorageService.getObject(anyString(), any())).thenReturn(data);
        var experimentHistory = createExperimentHistory(data);
        when(experimentProcessorService.processExperimentHistory(any(Experiment.class),
                any(InitializationParams.class))).thenReturn(experimentHistory);
        CalculationExecutorService executorService = mock(CalculationExecutorService.class);
        ReflectionTestUtils.setField(experimentModelProcessorStepHandler, "executorService", executorService);
        doThrow(TimeoutException.class).when(executorService)
                .execute(any(Callable.class), anyLong(), any(TimeUnit.class));
        testStep(experimentModelProcessorStepHandler::handle, ExperimentStepStatus.TIMEOUT);
    }
}
