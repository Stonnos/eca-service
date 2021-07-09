package com.ecaservice.service.scheduler;

import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.experiment.ExperimentRequestProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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

    private final ExperimentRepository experimentRepository;
    private final ExperimentRequestProcessor experimentRequestProcessor;

    /**
     * Processing new experiment requests.
     */
    @Scheduled(fixedDelayString = "${experiment.delaySeconds}000")
    public void processNewRequests() {
        log.trace("Starting to process new experiments.");
        Function<Pageable, Page<Experiment>> pageFunction =
                (pageable) -> experimentRepository.findExperimentsForProcessing(
                        Collections.singletonList(RequestStatus.NEW), pageable);
        processPaging(pageFunction, experiments -> {
            log.info("Obtained {} new experiments", experiments.size());
            experiments.forEach(experimentRequestProcessor::processNewExperiment);
        });
        log.info("New experiments processing has been successfully finished.");
    }

    /**
     * Processing experiment results sending to emails.
     */
    @Scheduled(fixedDelayString = "${experiment.delaySeconds}000")
    public void processRequestsToSent() {
        log.trace("Starting to sent experiment results.");
        Function<Pageable, Page<Experiment>> pageFunction =
                (pageable) -> experimentRepository.findExperimentsForProcessing(
                        Arrays.asList(RequestStatus.FINISHED, RequestStatus.ERROR, RequestStatus.TIMEOUT), pageable);
        processPaging(pageFunction,
                experiments -> experiments.forEach(experimentRequestProcessor::notifyExperimentFinished));
        log.trace("Sending experiments has been successfully finished.");
    }

    /**
     * Try to sent experiments results to ERS service.
     */
    @Scheduled(cron = "${experiment.ersSendingCron}")
    public void processRequestsToErs() {
        experimentRequestProcessor.sentExperimentResultsToErs();
    }

    /**
     * Removes experiments data files from disk. Schedules by cron.
     */
    @Scheduled(cron = "${experiment.removeExperimentCron}")
    public void processRequestsToRemove() {
        experimentRequestProcessor.removeExperimentsData();
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
