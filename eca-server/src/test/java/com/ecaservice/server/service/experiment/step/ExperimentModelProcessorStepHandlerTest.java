package com.ecaservice.server.service.experiment.step;

import com.ecaservice.s3.client.minio.exception.ObjectStorageException;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentStep;
import com.ecaservice.server.model.entity.ExperimentStepEntity;
import com.ecaservice.server.model.entity.ExperimentStepStatus;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.model.experiment.ExperimentContext;
import com.ecaservice.server.model.experiment.InitializationParams;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.repository.ExperimentStepRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.evaluation.CalculationExecutorService;
import com.ecaservice.server.service.evaluation.CalculationExecutorServiceImpl;
import com.ecaservice.server.service.experiment.ExperimentProcessorService;
import com.ecaservice.server.service.experiment.ExperimentStepService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.StopWatch;
import weka.core.Instances;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.ecaservice.server.TestHelperUtils.createExperimentHistory;
import static org.assertj.core.api.Assertions.assertThat;
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
class ExperimentModelProcessorStepHandlerTest extends AbstractJpaTest {

    @Mock
    private ObjectStorageService objectStorageService;
    @Mock
    private ExperimentProcessorService experimentProcessorService;

    @Inject
    private ExperimentConfig experimentConfig;
    @Inject
    private ExperimentStepService experimentStepService;
    @Inject
    private ExperimentStepRepository experimentStepRepository;
    @Inject
    private ExperimentRepository experimentRepository;

    private ExperimentModelProcessorStepHandler experimentModelProcessorStepHandler;

    private Instances data;
    private ExperimentStepEntity experimentStepEntity;

    @Override
    public void init() {
        data = TestHelperUtils.loadInstances();
        createAndSaveExperimentStep();
        var executorService = new CalculationExecutorServiceImpl(Executors.newCachedThreadPool());
        experimentModelProcessorStepHandler = new ExperimentModelProcessorStepHandler(experimentConfig,
                objectStorageService, experimentProcessorService, executorService, experimentStepService);
    }

    @Override
    public void deleteAll() {
        experimentStepRepository.deleteAll();
        experimentRepository.deleteAll();
    }

    @Test
    void testProcessExperimentWithSuccessStatus() throws Exception {
        when(objectStorageService.getObject(anyString(), any())).thenReturn(data);
        var experimentHistory = createExperimentHistory(data);
        when(experimentProcessorService.processExperimentHistory(any(Experiment.class),
                any(InitializationParams.class))).thenReturn(experimentHistory);
        internalTestStep(ExperimentStepStatus.COMPLETED);
    }

    @Test
    void testProcessExperimentWithErrorWhileGetInstances() throws Exception {
        when(objectStorageService.getObject(anyString(), any())).thenThrow(new RuntimeException());
        internalTestStep(ExperimentStepStatus.ERROR);
    }

    @Test
    void testProcessExperimentFailedWhileGetInstances() throws Exception {
        when(objectStorageService.getObject(anyString(), any())).thenThrow(
                new ObjectStorageException(new RuntimeException()));
        internalTestStep(ExperimentStepStatus.FAILED);
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
        internalTestStep(ExperimentStepStatus.TIMEOUT);
    }

    private void internalTestStep(ExperimentStepStatus expectedStatus) {
        ExperimentContext context = ExperimentContext.builder()
                .stopWatch(new StopWatch())
                .experiment(experimentStepEntity.getExperiment())
                .build();
        experimentModelProcessorStepHandler.handle(context, experimentStepEntity);
        verifyStepStatus(expectedStatus);
    }

    private void verifyStepStatus(ExperimentStepStatus expectedStatus) {
        var actualStep = experimentStepRepository.findById(experimentStepEntity.getId()).orElse(null);
        assertThat(actualStep).isNotNull();
        assertThat(actualStep.getStatus()).isEqualTo(expectedStatus);
    }

    private void createAndSaveExperimentStep() {
        var experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.IN_PROGRESS);
        experimentRepository.save(experiment);
        experimentStepEntity = TestHelperUtils.createExperimentStepEntity(experiment,
                ExperimentStep.EXPERIMENT_PROCESSING, ExperimentStepStatus.READY);
        experimentStepRepository.save(experimentStepEntity);
    }
}
