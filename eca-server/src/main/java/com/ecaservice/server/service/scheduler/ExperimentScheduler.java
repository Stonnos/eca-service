package com.ecaservice.server.service.scheduler;

import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.service.experiment.ExperimentDataCleaner;
import com.ecaservice.server.service.experiment.ExperimentProcessManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.function.Supplier;

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
    private final ExperimentDataCleaner experimentDataCleaner;
    private final ExperimentRepository experimentRepository;

    /**
     * Processes experiment requests.
     */
    @Scheduled(fixedDelayString = "${experiment.delaySeconds}000")
    public void processExperiments() {
        processNewRequests();
        processInProgressRequests();
        processRequestsToFinish();
    }

    /**
     * Removes experiments data files from S3. Schedules by cron.
     */
    @Scheduled(cron = "${app.removeModelCron}")
    public void processRequestsToRemove() {
        log.info("Starting job to removes experiments data files from disk");
        experimentDataCleaner.removeExperimentsTrainingData();
        experimentDataCleaner.removeExperimentsModels();
        log.info("Removing experiments data files job has been finished");
    }

    /**
     * Processing new experiment requests.
     */
    private void processNewRequests() {
        log.trace("Starting to process new experiments.");
        processExperiments(experimentRepository::findNewExperiments);
        log.trace("New experiments processing has been successfully finished.");
    }

    /**
     * Processing in progress experiment requests.
     */
    private void processInProgressRequests() {
        log.trace("Starting to process new experiments.");
        processExperiments(experimentRepository::findExperimentsToProcess);
        log.trace("New experiments processing has been successfully finished.");
    }

    /**
     * Processing experiment requests to finish.
     */
    private void processRequestsToFinish() {
        log.trace("Starting to process experiments to finish.");
        processExperiments(experimentRepository::findExperimentsToFinish);
        log.trace("Finished experiments processing has been successfully finished.");
    }

    private void processExperiments(Supplier<List<Long>> getExperimentsIdsSupplier) {
        var ids = getExperimentsIdsSupplier.get();
        if (!CollectionUtils.isEmpty(ids)) {
            log.info("Fetched [{}] experiments to process", ids.size());
            ids.forEach(experimentProcessManager::processExperiment);
        }
    }
}
