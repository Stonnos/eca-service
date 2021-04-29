package com.ecaservice.service.experiment;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentProgressEntity;
import com.ecaservice.repository.ExperimentProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Experiment progress service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class ExperimentProgressService {

    private static final int MAX_PROGRESS = 100;
    private static final int MIN_PROGRESS = 0;

    private final ExperimentProgressRepository experimentProgressRepository;

    /**
     * Starts experiment progress.
     *
     * @param experiment - experiment entity
     */
    public void start(Experiment experiment) {
        ExperimentProgressEntity experimentProgressEntity = getOrCreateExperimentProgress(experiment);
        experimentProgressEntity.setProgress(MIN_PROGRESS);
        experimentProgressRepository.save(experimentProgressEntity);
    }

    /**
     * Updates experiment progress.
     *
     * @param experiment - experiment entity
     * @param progress   - progress bar value
     */
    public void onProgress(Experiment experiment, @NotNull @Min(MIN_PROGRESS) @Max(MAX_PROGRESS) Integer progress) {
        ExperimentProgressEntity experimentProgressEntity = getOrCreateExperimentProgress(experiment);
        experimentProgressEntity.setProgress(progress);
        experimentProgressRepository.save(experimentProgressEntity);
    }

    /**
     * Finished experiment progress.
     *
     * @param experiment - experiment entity
     */
    public void finish(Experiment experiment) {
        ExperimentProgressEntity experimentProgressEntity = getOrCreateExperimentProgress(experiment);
        experimentProgressEntity.setProgress(MAX_PROGRESS);
        experimentProgressEntity.setFinished(true);
        experimentProgressRepository.save(experimentProgressEntity);
    }

    /**
     * Gets experiment progress entity.
     *
     * @param experiment - experiment entity
     * @return experiment progress entity
     */
    public ExperimentProgressEntity getExperimentProgress(Experiment experiment) {
        return experimentProgressRepository.findByExperiment(experiment)
                .orElseThrow(() -> new EntityNotFoundException(ExperimentProgressEntity.class,
                        String.format("Experiment id [%s]", experiment.getId())));
    }

    private ExperimentProgressEntity getOrCreateExperimentProgress(Experiment experiment) {
        ExperimentProgressEntity experimentProgressEntity = experimentProgressRepository.findByExperiment(experiment)
                .orElse(null);
        if (experimentProgressEntity == null) {
            experimentProgressEntity = new ExperimentProgressEntity();
            experimentProgressEntity.setExperiment(experiment);
        }
        return experimentProgressEntity;
    }
}
