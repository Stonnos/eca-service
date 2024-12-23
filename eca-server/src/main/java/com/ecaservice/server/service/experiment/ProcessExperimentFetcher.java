package com.ecaservice.server.service.experiment;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.repository.ExperimentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Process experiment requests fetcher.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessExperimentFetcher {

    private final ExperimentConfig experimentConfig;
    private final ExperimentRepository experimentRepository;

    /**
     * Gets next experiments for processing and sets a lock to prevent other threads from receiving the same data for
     * processing.
     *
     * @param pageable - pageable object
     * @return experiments page
     */
    @Locked(lockName = "getNextExperimentsToProcess")
    public Page<Experiment> getNextExperimentsToProcess(Pageable pageable) {
        log.debug("Starting to get next experiments to process");
        Page<Experiment> experiments = experimentRepository.findExperimentsToProcess(LocalDateTime.now(), pageable);
        if (experiments.hasContent()) {
            // Sets a lock to prevent other threads from receiving the same data for processing
            experiments.forEach(experiment -> experiment.setLockedTtl(
                    LocalDateTime.now().plusSeconds(experimentConfig.getLockTtlSeconds())));
            experimentRepository.saveAll(experiments);
            log.info("[{}] experiments to process has been fetched", experiments.getNumberOfElements());
        }
        log.debug("Next experiments to process has been fetched");
        return experiments;
    }
}
