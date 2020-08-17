package com.ecaservice.service.experiment;

import com.ecaservice.exception.EntityNotFoundException;
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
     * Updates experiment progress.
     *
     * @param experiment - experiment entity
     * @param progress   - progress bar value
     */
    public void onProgress(Experiment experiment, @NotNull @Min(MIN_PROGRESS) @Max(MAX_PROGRESS) Integer progress) {
        ExperimentProgressEntity experimentProgressEntity = getByExperiment(experiment);
        experimentProgressEntity.setProgress(progress);
        experimentProgressRepository.save(experimentProgressEntity);
    }

    /**
     * Finished experiment progress.
     *
     * @param experiment - experiment entity
     */
    public void done(Experiment experiment) {
        ExperimentProgressEntity experimentProgressEntity = getByExperiment(experiment);
        experimentProgressEntity.setFinished(true);
        experimentProgressEntity.setProgress(MAX_PROGRESS);
        experimentProgressRepository.save(experimentProgressEntity);
    }

    public ExperimentProgressEntity getByExperiment(Experiment experiment) {
        return experimentProgressRepository.findByExperiment(experiment).orElseThrow(
                () -> new EntityNotFoundException(ExperimentProgressEntity.class, experiment.getId()));
    }
}
