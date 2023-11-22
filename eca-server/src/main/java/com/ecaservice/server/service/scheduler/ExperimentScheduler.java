package com.ecaservice.server.service.scheduler;

import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.service.experiment.ExperimentDataCleaner;
import com.ecaservice.server.service.experiment.ExperimentProcessManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
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

    private final ExperimentConfig experimentConfig;
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
        processTimeoutRequest();
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

    /**
     * Processing new experiment requests.
     */
    private void processNewRequests() {
        log.trace("Starting to process new experiments.");
        processExperiments(experimentRepository::findNewExperiments, "Fetched new [{}] experiments to process");
        log.trace("New experiments processing has been successfully finished.");
    }

    /**
     * Processing in progress experiment requests.
     */
    private void processInProgressRequests() {
        log.trace("Starting to process in progress experiments.");
        LocalDateTime dateTime = LocalDateTime.now().minusMinutes(experimentConfig.getRequestTimeoutMinutes());
        processExperiments(() -> experimentRepository.findExperimentsToProcess(dateTime),
                "Fetched [{}] in progress experiments to process");
        log.trace("In progress experiments processing has been successfully finished.");
    }

    /**
     * Processing experiment requests to finish.
     */
    private void processRequestsToFinish() {
        log.trace("Starting to process experiments to finish.");
        processExperiments(experimentRepository::findExperimentsToFinish, "Fetched [{}] experiments to finish");
        log.trace("Finished experiments processing has been successfully finished.");
    }

    /**
     * Processing experiment timeout request to process again.
     */
    private void processTimeoutRequest() {
        log.trace("Starting to handle timeout experiments to process again.");
        LocalDateTime dateTime = LocalDateTime.now().minusMinutes(experimentConfig.getRequestTimeoutMinutes());
        processExperiments(() -> experimentRepository.findTimeoutExperimentsToProcess(dateTime),
                "Fetched [{}] timeout experiments to process again");
        log.trace("Timeout experiments processing has been successfully finished.");
    }

    private void processExperiments(Supplier<List<Long>> getExperimentsIdsSupplier, String infoLogMessage) {
        var ids = getExperimentsIdsSupplier.get();
        if (!CollectionUtils.isEmpty(ids)) {
            log.info(infoLogMessage, ids.size());
            ids.forEach(experimentProcessManager::processExperiment);
        }
    }
}
