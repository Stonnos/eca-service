package com.ecaservice.server.service.scheduler;

import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.service.experiment.ExperimentDataCleaner;
import com.ecaservice.server.service.experiment.ExperimentProcessManager;
import com.ecaservice.server.service.experiment.ProcessExperimentFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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

    private final ExperimentProcessManager experimentProcessManager;
    private final ProcessExperimentFetcher processExperimentFetcher;
    private final ExperimentDataCleaner experimentDataCleaner;

    /**
     * Processes experiment requests.
     */
    @Scheduled(fixedDelayString = "${experiment.delaySeconds}000")
    public void processExperiments() {
        log.debug("Starting job to process experiments");
        List<Experiment> experiments = processExperimentFetcher.getNextExperimentsToProcess();
        if (!CollectionUtils.isEmpty(experiments)) {
            experiments.forEach(experimentProcessManager::processExperiment);
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
}
