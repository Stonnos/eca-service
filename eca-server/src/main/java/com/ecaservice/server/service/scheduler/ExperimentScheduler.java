package com.ecaservice.server.service.scheduler;

import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.service.experiment.ExperimentRequestProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
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

    private static final List<RequestStatus> NEW_STATUSES = Collections.singletonList(RequestStatus.NEW);

    private final ExperimentRequestProcessor experimentRequestProcessor;
    private final ExperimentRepository experimentRepository;

    /**
     * Processing new experiment requests.
     */
    @Scheduled(fixedDelayString = "${experiment.delaySeconds}000")
    public void processNewRequests() {
        log.trace("Starting to process new experiments.");
        var newExperimentIds = experimentRepository.findExperimentsForProcessing(NEW_STATUSES);
        if (!CollectionUtils.isEmpty(newExperimentIds)) {
            log.info("Obtained {} new experiments", newExperimentIds.size());
            newExperimentIds.forEach(experimentRequestProcessor::processExperiment);
        }
        log.trace("New experiments processing has been successfully finished.");
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
}
