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
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.experiment.ExperimentDataCleaner;
import com.ecaservice.server.service.experiment.ExperimentProcessManager;
import com.ecaservice.server.service.experiment.ExperimentProgressService;
import com.ecaservice.server.service.experiment.ExperimentService;
import com.ecaservice.server.service.experiment.ExperimentRequestFetcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Unit tests that checks ExperimentScheduler functionality {@see ExperimentScheduler}.
 *
 * @author Roman Batygin
 */
@Import({ExperimentConfig.class, AppProperties.class, ExperimentRequestFetcher.class})
class ExperimentSchedulerTest extends AbstractJpaTest {

    @Autowired
    private ExperimentConfig experimentConfig;
    @Autowired
    private ExperimentRequestFetcher experimentRequestFetcher;
    @Autowired
    private ExperimentRepository experimentRepository;
    @Autowired
    private ExperimentStepRepository experimentStepRepository;
    @Autowired
    private InstancesInfoRepository instancesInfoRepository;

    @MockBean
    private ExperimentService experimentService;
    @MockBean
    private ExperimentProgressService experimentProgressService;
    @MockBean
    private ExperimentDataCleaner experimentDataCleaner;
    @MockBean
    private ApplicationEventPublisher eventPublisher;

    @MockBean
    private ExperimentProcessManager experimentProcessManager;

    private ExperimentScheduler experimentScheduler;

    @Override
    public void init() {
        experimentScheduler =
                new ExperimentScheduler(experimentConfig, experimentProcessManager, experimentRequestFetcher,
                        experimentDataCleaner);
    }

    @Override
    public void deleteAll() {
        experimentStepRepository.deleteAll();
        experimentRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    void testProcessNewExperiments() {
        var newExperiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        instancesInfoRepository.save(newExperiment.getInstancesInfo());
        experimentRepository.save(newExperiment);
        experimentScheduler.processExperiments();
        verify(experimentProcessManager, atLeastOnce()).processExperiment(any(Experiment.class));
    }

    @Test
    void testProcessExperiments() {
        var experiment = createAndSaveInProgressExperiment();
        createAndSaveExperimentStep(experiment, ExperimentStep.EXPERIMENT_PROCESSING, ExperimentStepStatus.FAILED);
        createAndSaveExperimentStep(experiment, ExperimentStep.UPLOAD_EXPERIMENT_MODEL, ExperimentStepStatus.READY);
        createAndSaveExperimentStep(experiment, ExperimentStep.GET_EXPERIMENT_DOWNLOAD_URL, ExperimentStepStatus.READY);
        experimentScheduler.processExperiments();
        verify(experimentProcessManager, atLeastOnce()).processExperiment(any(Experiment.class));
    }

    private Experiment createAndSaveInProgressExperiment() {
        var experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.IN_PROGRESS);
        instancesInfoRepository.save(experiment.getInstancesInfo());
        experiment.setStartDate(LocalDateTime.now());
        experimentRepository.save(experiment);
        return experiment;
    }

    private void createAndSaveExperimentStep(Experiment experiment,
                                             ExperimentStep experimentStep,
                                             ExperimentStepStatus stepStatus) {
        var experimentStepEntity = TestHelperUtils.createExperimentStepEntity(experiment, experimentStep, stepStatus);
        experimentStepRepository.save(experimentStepEntity);
    }
}
