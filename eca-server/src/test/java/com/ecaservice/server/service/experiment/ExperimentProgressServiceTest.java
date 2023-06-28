package com.ecaservice.server.service.experiment;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentProgressEntity;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.ExperimentProgressRepository;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.createExperiment;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for checking {@link ExperimentProgressService} functionality.
 *
 * @author Roman Batygin
 */
@Import(ExperimentProgressService.class)
class ExperimentProgressServiceTest extends AbstractJpaTest {

    private static final int FULL_PROGRESS = 100;
    private static final int PROGRESS_VALUE = 25;

    @Inject
    private ExperimentRepository experimentRepository;
    @Inject
    private InstancesInfoRepository instancesInfoRepository;
    @Inject
    private ExperimentProgressRepository experimentProgressRepository;
    @Inject
    private ExperimentProgressService experimentProgressService;

    @Override
    public void deleteAll() {
        experimentProgressRepository.deleteAll();
        experimentRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    void testStartProgress() {
        Experiment experiment = createAndSaveExperiment();
        experimentProgressService.start(experiment);
        ExperimentProgressEntity experimentProgressEntity =
                experimentProgressRepository.findByExperiment(experiment).orElse(null);
        assertThat(experimentProgressEntity).isNotNull();
        assertThat(experimentProgressEntity.getExperiment()).isNotNull();
        assertThat(experimentProgressEntity.getExperiment().getId()).isEqualTo(experiment.getId());
        assertThat(experimentProgressEntity.isFinished()).isFalse();
        assertThat(experimentProgressEntity.getProgress()).isZero();
    }

    @Test
    void testFinishedProgress() {
        Experiment experiment = createAndSaveExperiment();
        experimentProgressService.finish(experiment);
        ExperimentProgressEntity experimentProgressEntity =
                experimentProgressRepository.findByExperiment(experiment).orElse(null);
        assertThat(experimentProgressEntity).isNotNull();
        assertThat(experimentProgressEntity.isFinished()).isTrue();
        assertThat(experimentProgressEntity.getProgress()).isEqualTo(FULL_PROGRESS);
    }

    @Test
    void testOnProgress() {
        Experiment experiment = createAndSaveExperiment();
        experimentProgressService.onProgress(experiment, PROGRESS_VALUE);
        ExperimentProgressEntity experimentProgressEntity =
                experimentProgressRepository.findByExperiment(experiment).orElse(null);
        assertThat(experimentProgressEntity).isNotNull();
        assertThat(experimentProgressEntity.isFinished()).isFalse();
        assertThat(experimentProgressEntity.getProgress()).isEqualTo(PROGRESS_VALUE);
    }

    @Test
    void testGetExperimentProgress() {
        Experiment experiment = createAndSaveExperiment();
        experimentProgressService.start(experiment);
        ExperimentProgressEntity experimentProgressEntity = experimentProgressService.getExperimentProgress(experiment);
        assertThat(experimentProgressEntity).isNotNull();
        assertThat(experimentProgressEntity.getExperiment()).isNotNull();
        assertThat(experimentProgressEntity.getExperiment().getId()).isEqualTo(experiment.getId());
    }

    @Test
    void testGetExperimentProgressShouldThrowEntityNotFoundException() {
        Experiment experiment = createAndSaveExperiment();
        assertThrows(EntityNotFoundException.class, () ->  experimentProgressService.getExperimentProgress(experiment));
    }

    private Experiment createAndSaveExperiment() {
        Experiment experiment = createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        instancesInfoRepository.save(experiment.getInstancesInfo());
        return experimentRepository.save(experiment);
    }
}
