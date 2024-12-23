package com.ecaservice.server.service.experiment;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.repository.ExperimentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

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
     * @return experiments list
     */
    @Locked(lockName = "getNextExperimentsToProcess")
    public List<Experiment> getNextExperimentsToProcess() {
        log.debug("Starting to get next experiments to process");
        List<Experiment> experiments = experimentRepository.findExperimentsToProcess(LocalDateTime.now(),
                PageRequest.of(0, experimentConfig.getMaxRequestsPerJob()));
        if (!CollectionUtils.isEmpty(experiments)) {
            // Sets a lock to prevent other threads from receiving the same data for processing
            experiments.forEach(experiment -> experiment.setLockedTtl(
                    LocalDateTime.now().plusSeconds(experimentConfig.getLockTtlSeconds())));
            experimentRepository.saveAll(experiments);
            log.info("[{}] experiments to process has been fetched", experiments.size());
        }
        log.debug("Next experiments to process has been fetched");
        return experiments;
    }
}
