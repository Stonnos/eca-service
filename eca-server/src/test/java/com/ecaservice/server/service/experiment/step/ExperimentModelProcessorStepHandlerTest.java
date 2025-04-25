package com.ecaservice.server.service.experiment.step;

import com.ecaservice.s3.client.minio.exception.ObjectStorageException;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.mapping.ExperimentProgressMapperImpl;
import com.ecaservice.server.model.CancelableTask;
import com.ecaservice.server.model.EvaluationStatus;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentStepStatus;
import com.ecaservice.server.model.experiment.ExperimentProcessResult;
import com.ecaservice.server.model.experiment.InitializationParams;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.service.data.InstancesLoaderService;
import com.ecaservice.server.service.experiment.ExperimentModelLocalStorage;
import com.ecaservice.server.service.experiment.ExperimentProcessorService;
import com.ecaservice.server.service.experiment.ExperimentProgressService;
import com.ecaservice.server.service.experiment.ExperimentStepService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import weka.core.Instances;

import java.util.concurrent.Executors;

import static com.ecaservice.server.TestHelperUtils.createExperimentHistory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ExperimentModelProcessorStepHandler} class.
 *
 * @author Roman Batygin
 */
@Import({ExperimentConfig.class, ExperimentStepService.class, ExperimentProgressService.class,
        ExperimentProgressMapperImpl.class})
class ExperimentModelProcessorStepHandlerTest extends AbstractStepHandlerTest {

    private static final int FULL_PROGRESS = 100;

    @Mock
    private InstancesLoaderService instancesLoaderService;
    @Mock
    private ExperimentProcessorService experimentProcessorService;
    @Mock
    private ExperimentModelLocalStorage experimentModelLocalStorage;

    @Autowired
    private ExperimentConfig experimentConfig;
    @Autowired
    private ExperimentStepService experimentStepService;
    @Autowired
    private ExperimentProgressService experimentProgressService;
    @Autowired
    private ExperimentRepository experimentRepository;

    private ExperimentModelProcessorStepHandler experimentModelProcessorStepHandler;

    private Instances data;

    @Override
    public void init() {
        super.init();
        data = TestHelperUtils.loadInstances();
        var executorService = Executors.newCachedThreadPool();
        experimentModelProcessorStepHandler =
                new ExperimentModelProcessorStepHandler(experimentConfig, instancesLoaderService,
                        experimentProcessorService, executorService, experimentStepService, experimentModelLocalStorage,
                        experimentProgressService, experimentRepository);
    }

    @Test
    void testProcessExperimentWithSuccessStatus() {
        when(instancesLoaderService.loadInstances(anyString())).thenReturn(data);
        var experimentHistory = createExperimentHistory(data);
        ExperimentProcessResult experimentProcessResult = new ExperimentProcessResult();
        experimentProcessResult.setExperimentHistory(experimentHistory);
        experimentProcessResult.setEvaluationStatus(EvaluationStatus.SUCCESS);
        when(experimentProcessorService.processExperimentHistory(any(Experiment.class),
                any(CancelableTask.class), any(InitializationParams.class))).thenReturn(experimentProcessResult);
        experimentProgressService.start(getExperimentStepEntity().getExperiment());
        testStep(experimentModelProcessorStepHandler::handle, ExperimentStepStatus.COMPLETED);
        verifyProgressFinished();
        var actualExperiment =
                experimentRepository.findById(getExperimentStepEntity().getExperiment().getId()).orElse(null);
        assertThat(actualExperiment).isNotNull();
        assertThat(actualExperiment.getMaxPctCorrect()).isNotNull();
    }

    @Test
    void testProcessExperimentWithErrorWhileGetInstances() {
        when(instancesLoaderService.loadInstances(anyString())).thenThrow(new RuntimeException());
        testStep(experimentModelProcessorStepHandler::handle, ExperimentStepStatus.ERROR);
    }

    @Test
    void testProcessExperimentFailedWhileGetInstances() {
        when(instancesLoaderService.loadInstances(anyString()))
                .thenThrow(new ObjectStorageException(new RuntimeException()));
        testStep(experimentModelProcessorStepHandler::handle, ExperimentStepStatus.FAILED);
    }

    private void verifyProgressFinished() {
        var experimentProgressEntity =
                getExperimentProgressRepository().findByExperiment(getExperimentStepEntity().getExperiment()).orElse(
                        null);
        assertThat(experimentProgressEntity).isNotNull();
        assertThat(experimentProgressEntity.isFinished()).isTrue();
        assertThat(experimentProgressEntity.getProgress()).isEqualTo(FULL_PROGRESS);
    }
}
