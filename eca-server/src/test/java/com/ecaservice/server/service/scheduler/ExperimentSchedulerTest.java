package com.ecaservice.server.service.scheduler;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentStep;
import com.ecaservice.server.model.entity.ExperimentStepStatus;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.repository.ExperimentStepRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.experiment.ExperimentProgressService;
import com.ecaservice.server.service.experiment.ExperimentRequestProcessor;
import com.ecaservice.server.service.experiment.ExperimentService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.UUID;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests that checks ExperimentScheduler functionality {@see ExperimentScheduler}.
 *
 * @author Roman Batygin
 */
@Import({ExperimentConfig.class, AppProperties.class})
class ExperimentSchedulerTest extends AbstractJpaTest {

    @Inject
    private ExperimentRepository experimentRepository;
    @Inject
    private ExperimentStepRepository experimentStepRepository;

    @MockBean
    private ExperimentService experimentService;
    @MockBean
    private ExperimentProgressService experimentProgressService;
    @MockBean
    private ApplicationEventPublisher eventPublisher;

    @MockBean
    private ExperimentRequestProcessor experimentRequestProcessor;

    private ExperimentScheduler experimentScheduler;

    @Override
    public void init() {
        experimentScheduler = new ExperimentScheduler(experimentRequestProcessor, experimentRepository);
    }

    @Override
    public void deleteAll() {
        experimentStepRepository.deleteAll();
        experimentRepository.deleteAll();
    }

    @Test
    void testProcessNewExperiments() {
        var newExperiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experimentRepository.save(newExperiment);
        when(experimentService.getById(newExperiment.getId())).thenReturn(newExperiment);
        experimentScheduler.processExperiments();
        verify(experimentRequestProcessor, atLeastOnce()).startExperiment(newExperiment.getId());
    }

    @Test
    void testProcessExperiments() {
        var experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.IN_PROGRESS);
        experimentRepository.save(experiment);
        createAndSaveExperimentStep(experiment, ExperimentStep.EXPERIMENT_PROCESSING, ExperimentStepStatus.FAILED);
        createAndSaveExperimentStep(experiment, ExperimentStep.UPLOAD_EXPERIMENT_MODEL, ExperimentStepStatus.READY);
        createAndSaveExperimentStep(experiment, ExperimentStep.GET_EXPERIMENT_DOWNLOAD_URL, ExperimentStepStatus.READY);
        when(experimentService.getById(experiment.getId())).thenReturn(experiment);
        experimentScheduler.processExperiments();
        verify(experimentRequestProcessor, never()).finishExperiment(experiment.getId());
        verify(experimentRequestProcessor, atLeastOnce()).processExperiment(experiment.getId());
    }

    @Test
    void testProcessFinishedExperimentsWithErrorStep() {
        var experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.IN_PROGRESS);
        experimentRepository.save(experiment);
        createAndSaveExperimentStep(experiment, ExperimentStep.EXPERIMENT_PROCESSING, ExperimentStepStatus.ERROR);
        createAndSaveExperimentStep(experiment, ExperimentStep.UPLOAD_EXPERIMENT_MODEL, ExperimentStepStatus.CANCELED);
        createAndSaveExperimentStep(experiment, ExperimentStep.GET_EXPERIMENT_DOWNLOAD_URL,
                ExperimentStepStatus.CANCELED);
        when(experimentService.getById(experiment.getId())).thenReturn(experiment);
        experimentScheduler.processExperiments();
        verify(experimentRequestProcessor, never()).processExperiment(experiment.getId());
        verify(experimentRequestProcessor, atLeastOnce()).finishExperiment(experiment.getId());
    }

    @Test
    void testProcessFinishedExperimentsWithAllCompletedSteps() {
        var experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.IN_PROGRESS);
        experimentRepository.save(experiment);
        createAndSaveExperimentStep(experiment, ExperimentStep.EXPERIMENT_PROCESSING, ExperimentStepStatus.COMPLETED);
        createAndSaveExperimentStep(experiment, ExperimentStep.UPLOAD_EXPERIMENT_MODEL, ExperimentStepStatus.COMPLETED);
        createAndSaveExperimentStep(experiment, ExperimentStep.GET_EXPERIMENT_DOWNLOAD_URL,
                ExperimentStepStatus.COMPLETED);
        when(experimentService.getById(experiment.getId())).thenReturn(experiment);
        experimentScheduler.processExperiments();
        verify(experimentRequestProcessor, never()).processExperiment(experiment.getId());
        verify(experimentRequestProcessor, atLeastOnce()).finishExperiment(experiment.getId());
    }

    @Test
    void testProcessFinishedExperimentsWithNotAllCompleted() {
        var experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.IN_PROGRESS);
        experimentRepository.save(experiment);
        createAndSaveExperimentStep(experiment, ExperimentStep.EXPERIMENT_PROCESSING, ExperimentStepStatus.COMPLETED);
        createAndSaveExperimentStep(experiment, ExperimentStep.UPLOAD_EXPERIMENT_MODEL, ExperimentStepStatus.COMPLETED);
        createAndSaveExperimentStep(experiment, ExperimentStep.GET_EXPERIMENT_DOWNLOAD_URL, ExperimentStepStatus.READY);
        when(experimentService.getById(experiment.getId())).thenReturn(experiment);
        experimentScheduler.processExperiments();
        verify(experimentRequestProcessor, atLeastOnce()).processExperiment(experiment.getId());
        verify(experimentRequestProcessor, never()).finishExperiment(experiment.getId());
    }

    private void createAndSaveExperimentStep(Experiment experiment,
                                             ExperimentStep experimentStep,
                                             ExperimentStepStatus stepStatus) {
        var experimentStepEntity = TestHelperUtils.createExperimentStepEntity(experiment, experimentStep, stepStatus);
        experimentStepRepository.save(experimentStepEntity);
    }
}
