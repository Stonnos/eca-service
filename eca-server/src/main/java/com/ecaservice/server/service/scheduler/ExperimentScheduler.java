package com.ecaservice.server.service.scheduler;

import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.service.experiment.ExperimentDataCleaner;
import com.ecaservice.server.service.experiment.ExperimentProcessManager;
import com.ecaservice.server.service.experiment.ExperimentRequestFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Experiment scheduler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentScheduler {

    private final ExperimentConfig experimentConfig;
    private final ExperimentProcessManager experimentProcessManager;
    private final ExperimentRequestFetcher experimentRequestFetcher;
    private final ExperimentDataCleaner experimentDataCleaner;
    private final ExperimentRepository experimentRepository;

    /**
     * Processes experiment requests.
     */
    @Scheduled(fixedDelayString = "${experiment.delaySeconds}000")
    public void processExperiments() {
        log.debug("Starting job to process experiments");
        Pageable pageRequest = PageRequest.of(0, experimentConfig.getBatchSize());
        List<Experiment> experiments;
        while (!(experiments = experimentRequestFetcher.lockNextExperimentsToProcess(pageRequest)).isEmpty()) {
            processExperiments(experiments);
        }
        log.debug("Process experiments job has been finished");
    }

    /**
     * Removes experiments data files from S3. Schedules by cron.
     */
    @Scheduled(cron = "${app.removeModelCron}")
    public void processRequestsToRemove() {
        log.info("Starting job to removes experiments data files from disk");
        experimentDataCleaner.removeExperimentsModels();
        log.info("Removing experiments data files job has been finished");
    }

    private void processExperiments(List<Experiment> experiments) {
        experiments.forEach(experiment -> {
            experimentProcessManager.processExperiment(experiment);
            experimentRepository.resetLock(experiment.getId());
        });
    }
}
