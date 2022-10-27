package com.ecaservice.server.service.scheduler;

import com.ecaservice.server.model.entity.ExperimentStepStatus;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.service.experiment.ExperimentRequestProcessor;
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

    private static final List<ExperimentStepStatus> FINAL_EXPERIMENT_STEP_STATUSES =
            List.of(ExperimentStepStatus.COMPLETED, ExperimentStepStatus.ERROR);
    private static final List<ExperimentStepStatus> EXPERIMENT_STEP_STATUSES_TO_PROCESS =
            List.of(ExperimentStepStatus.READY, ExperimentStepStatus.FAILED);

    private final ExperimentRequestProcessor experimentRequestProcessor;
    private final ExperimentRepository experimentRepository;

    /**
     * Processes experiment requests.
     */
    @Scheduled(fixedDelayString = "${experiment.delaySeconds}000")
    public void processExperiments() {
        processNewRequests();
        processInProgressRequests();
        processFinishedRequests();
    }

    /**
     * Removes experiments data files from disk. Schedules by cron.
     */
    @Scheduled(cron = "${experiment.removeExperimentCron}")
    public void processRequestsToRemove() {
        log.info("Starting job to removes experiments data files from disk");
        experimentRequestProcessor.removeExperimentsTrainingData();
        experimentRequestProcessor.removeExperimentsModels();
        log.info("Removing experiments data files job has been finished");
    }

    /**
     * Processing new experiment requests.
     */
    private void processNewRequests() {
        log.trace("Starting to process new experiments.");
        var newExperimentIds = experimentRepository.findNewExperiments();
        if (!CollectionUtils.isEmpty(newExperimentIds)) {
            log.info("Fetched {} new experiments", newExperimentIds.size());
            newExperimentIds.forEach(experimentRequestProcessor::startExperiment);
        }
        log.trace("New experiments processing has been successfully finished.");
    }

    /**
     * Processing in progress experiment requests.
     */
    private void processInProgressRequests() {
        log.trace("Starting to process new experiments.");
        var ids = experimentRepository.findExperimentsToProcess(FINAL_EXPERIMENT_STEP_STATUSES);
        if (!CollectionUtils.isEmpty(ids)) {
            log.info("Fetched {} experiments to process", ids.size());
            ids.forEach(experimentRequestProcessor::processExperiment);
        }
        log.trace("New experiments processing has been successfully finished.");
    }

    /**
     * Processing finished experiment requests.
     */
    private void processFinishedRequests() {
        log.trace("Starting to process finished experiments.");
        var ids = experimentRepository.findExperimentsToProcess(EXPERIMENT_STEP_STATUSES_TO_PROCESS);
        if (!CollectionUtils.isEmpty(ids)) {
            log.info("Fetched {} finished experiments", ids.size());
            ids.forEach(experimentRequestProcessor::finishExperiment);
        }
        log.trace("Finished experiments processing has been successfully finished.");
    }
}
