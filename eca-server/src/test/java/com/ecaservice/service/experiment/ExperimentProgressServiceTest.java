package com.ecaservice.service.experiment;

import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentProgressEntity;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.repository.ExperimentProgressRepository;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.AbstractJpaTest;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.UUID;

import static com.ecaservice.TestHelperUtils.createExperiment;
import static org.assertj.core.api.Assertions.assertThat;

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
    private ExperimentProgressRepository experimentProgressRepository;
    @Inject
    private ExperimentProgressService experimentProgressService;

    @Override
    public void deleteAll() {
        experimentProgressRepository.deleteAll();
        experimentRepository.deleteAll();
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

    private Experiment createAndSaveExperiment() {
        Experiment experiment = createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        return experimentRepository.save(experiment);
    }
}
