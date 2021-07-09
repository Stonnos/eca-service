package com.ecaservice.service.scheduler;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.core.lock.fallback.FallbackHandler;
import com.ecaservice.core.lock.service.LockService;
import com.ecaservice.event.model.ExperimentEmailEvent;
import com.ecaservice.event.model.ExperimentFinishedEvent;
import com.ecaservice.event.model.ExperimentWebPushEvent;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.repository.ExperimentResultsEntityRepository;
import com.ecaservice.service.ers.ErsService;
import com.ecaservice.service.experiment.ExperimentProgressService;
import com.ecaservice.service.experiment.ExperimentService;
import eca.converters.model.ExperimentHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ecaservice.common.web.util.LogHelper.EV_REQUEST_ID;
import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;

/**
 * Experiment scheduler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentScheduler {

    private static final String EXPERIMENTS_CRON_JOB_KEY = "experimentsCronJob";

    private final ExperimentRepository experimentRepository;
    private final ExperimentResultsEntityRepository experimentResultsEntityRepository;
    private final ExperimentService experimentService;
    private final ApplicationEventPublisher eventPublisher;
    private final ErsService ersService;
    private final ExperimentProgressService experimentProgressService;
    private final LockService experimentLockService;
    private final ExperimentConfig experimentConfig;

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
            experiments.forEach(experiment -> {
                FallbackHandler processCallback = () -> processNewExperiment(experiment);
                FallbackHandler lockAcquiredCallback =
                        () -> log.info("Experiment [{}] is already been processed. Skip...", experiment.getRequestId());
                experimentLockService.doInLock(experiment.getRequestId(), processCallback, lockAcquiredCallback);
            });
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
        processPaging(pageFunction, experiments -> {
            experiments.forEach(experiment -> {
                FallbackHandler processCallback = () -> {
                    putMdc(TX_ID, experiment.getRequestId());
                    putMdc(EV_REQUEST_ID, experiment.getRequestId());
                    eventPublisher.publishEvent(new ExperimentEmailEvent(this, experiment));
                };
                FallbackHandler lockAcquiredCallback =
                        () -> log.info("Experiment [{}] is already been processed. Skip...", experiment.getRequestId());
                experimentLockService.doInLock(experiment.getRequestId(), processCallback, lockAcquiredCallback);

            });
        });
        log.trace("Sending experiments has been successfully finished.");
    }

    /**
     * Try to sent experiments results to ERS service.
     */
    @Scheduled(cron = "${experiment.ersSendingCron}")
    public void processRequestsToErs() {
        log.info("Starting to sent experiment results to ERS service");
        FallbackHandler lockAcquiredCallback =
                () -> log.info("Job with key [{}] is already processed. Skip...", EXPERIMENTS_CRON_JOB_KEY);
        experimentLockService.doInLock(EXPERIMENTS_CRON_JOB_KEY, this::sentExperimentResultsToErs,
                lockAcquiredCallback);
        log.info("Finished to sent experiment results to ERS service");
    }

    /**
     * Removes experiments data files from disk. Schedules by cron.
     */
    @Scheduled(cron = "${experiment.removeExperimentCron}")
    public void processRequestsToRemove() {
        log.info("Starting to remove experiments data.");
        FallbackHandler lockAcquiredCallback =
                () -> log.info("Job with key [{}] is already processed. Skip...", EXPERIMENTS_CRON_JOB_KEY);
        experimentLockService.doInLock(EXPERIMENTS_CRON_JOB_KEY, this::removeExperimentsData, lockAcquiredCallback);
        log.info("Experiments data removing has been finished.");
    }

    private void processNewExperiment(Experiment experiment) {
        putMdc(TX_ID, experiment.getRequestId());
        putMdc(EV_REQUEST_ID, experiment.getRequestId());
        experimentProgressService.start(experiment);
        setInProgressStatus(experiment);
        ExperimentHistory experimentHistory = experimentService.processExperiment(experiment);
        eventPublisher.publishEvent(new ExperimentWebPushEvent(this, experiment));
        eventPublisher.publishEvent(new ExperimentEmailEvent(this, experiment));
        if (RequestStatus.FINISHED.equals(experiment.getRequestStatus())) {
            eventPublisher.publishEvent(new ExperimentFinishedEvent(this, experiment, experimentHistory));
        }
        experimentProgressService.finish(experiment);
    }

    private void setInProgressStatus(Experiment experiment) {
        experiment.setRequestStatus(RequestStatus.IN_PROGRESS);
        experiment.setStartDate(LocalDateTime.now());
        experimentRepository.save(experiment);
        log.info("Experiment [{}] in progress status has been set", experiment.getRequestId());
        eventPublisher.publishEvent(new ExperimentWebPushEvent(this, experiment));
        eventPublisher.publishEvent(new ExperimentEmailEvent(this, experiment));
    }

    private void removeExperimentsData() {
        LocalDateTime dateTime = LocalDateTime.now().minusDays(experimentConfig.getNumberOfDaysForStorage());
        List<Experiment> experiments = experimentRepository.findNotDeletedExperiments(dateTime);
        log.trace("Obtained {} experiments to remove data", experiments.size());
        experiments.forEach(experiment -> {
            putMdc(TX_ID, experiment.getRequestId());
            putMdc(EV_REQUEST_ID, experiment.getRequestId());
            experimentService.removeExperimentData(experiment);
        });
    }

    private void sentExperimentResultsToErs() {
        List<ExperimentResultsEntity> experimentResultsEntities =
                experimentResultsEntityRepository.findExperimentsResultsToErsSent();
        log.trace("Obtained {} experiments results sending to ERS service", experimentResultsEntities.size());
        Map<Experiment, List<ExperimentResultsEntity>> experimentResultsMap = experimentResultsEntities
                .stream()
                .collect(Collectors.groupingBy(ExperimentResultsEntity::getExperiment));
        experimentResultsMap.forEach((experiment, experimentResultsEntityList) -> {
            putMdc(TX_ID, experiment.getRequestId());
            putMdc(EV_REQUEST_ID, experiment.getRequestId());
            try {
                ExperimentHistory experimentHistory = experimentService.getExperimentHistory(experiment);
                experimentResultsEntityList.forEach(
                        experimentResultsEntity -> ersService.sentExperimentResults(experimentResultsEntity,
                                experimentHistory, ExperimentResultsRequestSource.SYSTEM));
            } catch (Exception ex) {
                log.error("There was an error while sending experiment [{}] history: {}", experiment.getRequestId(),
                        ex.getMessage());
            }
        });
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
