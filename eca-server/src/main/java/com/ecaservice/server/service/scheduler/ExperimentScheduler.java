package com.ecaservice.server.service.scheduler;

import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.service.experiment.ExperimentRequestProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

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

    private final ExperimentRepository experimentRepository;
    private final ExperimentRequestProcessor experimentRequestProcessor;

    /**
     * Processing new experiment requests.
     */
    @Scheduled(fixedDelayString = "${experiment.delaySeconds}000")
    public void processNewRequests() {
        log.trace("Starting to process new experiments.");
        Function<Pageable, Page<Experiment>> pageFunction =
                pageable -> experimentRepository.findExperimentsForProcessing(NEW_STATUSES, pageable);
        processPaging(pageFunction, experiments -> {
            log.info("Obtained {} new experiments", experiments.size());
            experiments.forEach(experimentRequestProcessor::processNewExperiment);
        });
        log.trace("New experiments processing has been successfully finished.");
    }

    /**
     * Try to sent experiments results to ERS service.
     */
    @Scheduled(cron = "${experiment.ersSendingCron}")
    public void processRequestsToErs() {
        log.info("Starting job to sent experiments results to ERS service");
        experimentRequestProcessor.sentExperimentResultsToErs();
        log.info("Experiments results sending job has been finished");
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

    private <T> void processPaging(Function<Pageable, Page<T>> pageFunction, Consumer<List<T>> pageContentAction) {
        Pageable pageRequest = PageRequest.of(0, 1);
        Page<T> page;
        do {
            page = pageFunction.apply(pageRequest);
            if (page == null || !page.hasContent()) {
                log.trace("No one requests has been fetched");
                break;
            } else {
                pageContentAction.accept(page.getContent());
            }
            pageRequest = page.nextPageable();
        } while (page.hasNext());
    }
}
