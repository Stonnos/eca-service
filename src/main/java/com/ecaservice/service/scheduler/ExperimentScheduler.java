package com.ecaservice.service.scheduler;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsRequest;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.model.experiment.ExperimentStatus;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.ers.ErsRequestService;
import com.ecaservice.service.PageableCallback;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.service.experiment.mail.NotificationService;
import eca.converters.model.ExperimentHistory;
import eca.core.evaluation.EvaluationResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Experiment scheduler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ExperimentScheduler {

    private final ExperimentRepository experimentRepository;
    private final ExperimentService experimentService;
    private final NotificationService notificationService;
    private final ErsRequestService ersRequestService;
    private final ExperimentConfig experimentConfig;

    /**
     * Constructor with dependency spring injection.
     *
     * @param experimentRepository - experiment repository bean
     * @param experimentService    - experiment service bean
     * @param notificationService  - notification service bean
     * @param ersRequestService    - ers request service bean
     * @param experimentConfig     - experiment config bean
     */
    @Inject
    public ExperimentScheduler(ExperimentRepository experimentRepository,
                               ExperimentService experimentService,
                               NotificationService notificationService,
                               ErsRequestService ersRequestService,
                               ExperimentConfig experimentConfig) {
        this.experimentRepository = experimentRepository;
        this.experimentService = experimentService;
        this.notificationService = notificationService;
        this.ersRequestService = ersRequestService;
        this.experimentConfig = experimentConfig;
    }

    /**
     * Processing new experiment requests.
     */
    @Scheduled(fixedDelayString = "${experiment.delaySeconds}000")
    public void processNewRequests() {
        log.trace("Starting to built experiments.");
        processExperiments(new PageableCallback<Experiment>() {
            @Override
            public void perform(List<Experiment> experiments) {
                experiments.forEach(experiment -> {
                    ExperimentHistory experimentHistory = experimentService.processExperiment(experiment);
                    if (ExperimentStatus.FINISHED.equals(experiment.getExperimentStatus())) {
                        List<EvaluationResults> evaluationResults = experimentHistory.getExperiment();
                        int resultsSize = Integer.min(evaluationResults.size(), experimentConfig.getResultSizeToSend());
                        evaluationResults.stream().limit(resultsSize).forEach(results -> {
                            ExperimentResultsRequest experimentResultsRequest = new ExperimentResultsRequest();
                            experimentResultsRequest.setRequestSource(ExperimentResultsRequestSource.SYSTEM);
                            experimentResultsRequest.setExperiment(experiment);
                            ersRequestService.saveEvaluationResults(results, experimentResultsRequest);
                        });
                    }
                });
            }

            @Override
            public Page<Experiment> findNextPage(Pageable pageable) {
                return experimentRepository.findNotSentExperiments(Collections.singletonList(ExperimentStatus.NEW),
                        pageable);
            }
        });
        log.trace("Building experiments has been successfully finished.");
    }

    /**
     * Processing experiment requests for sending.
     */
    @Scheduled(fixedDelayString = "${experiment.delaySeconds}000")
    public void processRequestsToSent() {
        log.trace("Starting to sent experiment results.");
        processExperiments(new PageableCallback<Experiment>() {
            @Override
            public void perform(List<Experiment> experiments) {
                experiments.forEach(notificationService::notifyByEmail);
            }

            @Override
            public Page<Experiment> findNextPage(Pageable pageable) {
                return experimentRepository.findNotSentExperiments(
                        Arrays.asList(ExperimentStatus.FINISHED, ExperimentStatus.ERROR, ExperimentStatus.TIMEOUT),
                        pageable);
            }
        });
        log.trace("Sending experiments has been successfully finished.");
    }

    /**
     * Removes experiments data files from disk. Schedules by cron.
     */
    @Scheduled(cron = "${experiment.removeExperimentCron}")
    public void processRequestsToRemove() {
        log.info("Starting to remove experiments data.");
        final LocalDateTime dateTime = LocalDateTime.now().minusDays(experimentConfig.getNumberOfDaysForStorage());
        processExperiments(new PageableCallback<Experiment>() {
            @Override
            public void perform(List<Experiment> experiments) {
                experiments.forEach(experimentService::removeExperimentData);
            }

            @Override
            public Page<Experiment> findNextPage(Pageable pageable) {
                return experimentRepository.findNotDeletedExperiments(dateTime, pageable);
            }
        });
        log.info("Experiments data removing has been finished.");
    }

    /**
     * Processes experiments using pagination. Method <code>findNextPage</code> always takes
     * the initial page request, because experiments fields are updated at each iteration.
     *
     * @param pageableCallback callback {@link PageableCallback}
     */
    private <T> void processExperiments(PageableCallback<T> pageableCallback) {
        Pageable pageable = new PageRequest(0, experimentConfig.getPageSize());
        Page<T> page;
        int totalCount = 0;
        do {
            page = pageableCallback.findNextPage(pageable);
            if (!page.hasContent()) {
                break;
            }
            log.trace("Obtained new experiments page [{}, {}] for processing.", page.getNumber(), page.getSize());
            pageableCallback.perform(page.getContent());
            totalCount += page.getSize();
            log.trace("Experiments page [{}, {}] has been processed.", page.getNumber(), page.getSize());
        } while (page.hasNext());
        log.trace("{} experiments has been processed.", totalCount);
    }

}
