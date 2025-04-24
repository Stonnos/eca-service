package com.ecaservice.server.service.experiment;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.mapping.ExperimentProgressMapper;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentProgressEntity;
import com.ecaservice.server.model.experiment.ExperimentProgressData;
import com.ecaservice.server.repository.ExperimentProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static com.ecaservice.server.config.cache.CacheNames.EXPERIMENT_PROGRESS_CACHE;

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
    private final ExperimentProgressMapper experimentProgressMapper;

    /**
     * Starts experiment progress.
     *
     * @param experiment - experiment entity
     */
    public void start(Experiment experiment) {
        log.info("Starting experiment [{}] progress bar", experiment.getRequestId());
        var experimentProgressEntity = experimentProgressRepository.findByExperiment(experiment)
                .orElse(null);
        if (experimentProgressEntity == null) {
            experimentProgressEntity = new ExperimentProgressEntity();
            experimentProgressEntity.setExperiment(experiment);
        }
        experimentProgressEntity.setProgress(MIN_PROGRESS);
        experimentProgressRepository.save(experimentProgressEntity);
        log.info("Experiment progress [{}] has been started", experiment.getRequestId());
    }

    /**
     * Updates experiment progress.
     *
     * @param experiment - experiment entity
     * @param progress   - progress bar value
     */
    @Transactional
    public void onProgress(Experiment experiment, Integer progress) {
        log.debug("Update experiment [{}] progress bar with value {}", experiment.getRequestId(), progress);
        experimentProgressRepository.updateProgress(experiment, progress);
    }

    /**
     * Finished experiment progress.
     *
     * @param experiment - experiment entity
     */
    public void finish(Experiment experiment) {
        log.info("Finished experiment [{}] progress", experiment.getRequestId());
        ExperimentProgressEntity experimentProgressEntity = getExperimentProgress(experiment);
        experimentProgressEntity.setProgress(MAX_PROGRESS);
        experimentProgressEntity.setFinished(true);
        experimentProgressRepository.save(experimentProgressEntity);
    }

    /**
     * Cancel experiment progress.
     *
     * @param experiment - experiment entity
     */
    @CachePut(value = EXPERIMENT_PROGRESS_CACHE, key = "#experiment.id")
    public ExperimentProgressData cancel(Experiment experiment) {
        log.info("Cancel experiment [{}] progress", experiment.getRequestId());
        ExperimentProgressEntity experimentProgressEntity = getExperimentProgress(experiment);
        experimentProgressEntity.setCanceled(true);
        experimentProgressRepository.save(experimentProgressEntity);
        return experimentProgressMapper.mapToExperimentData(experimentProgressEntity);
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

    /**
     * Gets experiment progress data.
     *
     * @param experiment - experiment entity
     * @return experiment progress data
     */
    @Cacheable(value = EXPERIMENT_PROGRESS_CACHE, key = "#experiment.id")
    public ExperimentProgressData getExperimentProgressData(Experiment experiment) {
        log.info("Gets experiment [{}] progress data", experiment.getRequestId());
        ExperimentProgressEntity experimentProgressEntity = getExperimentProgress(experiment);
        ExperimentProgressData experimentProgressData =
                experimentProgressMapper.mapToExperimentData(experimentProgressEntity);
        log.info("Experiment [{}] progress data has been fetched", experiment.getRequestId());
        return experimentProgressData;
    }
}
