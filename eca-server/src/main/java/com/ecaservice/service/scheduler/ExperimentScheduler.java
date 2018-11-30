package com.ecaservice.service.scheduler;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsRequest;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.PageableCallback;
import com.ecaservice.service.ers.ErsService;
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
    private final ErsService ersService;
    private final ExperimentConfig experimentConfig;

    /**
     * Constructor with dependency spring injection.
     *
     * @param experimentRepository - experiment repository bean
     * @param experimentService    - experiment service bean
     * @param notificationService  - notification service bean
     * @param ersService           - ers service bean
     * @param experimentConfig     - experiment config bean
     */
    @Inject
    public ExperimentScheduler(ExperimentRepository experimentRepository,
                               ExperimentService experimentService,
                               NotificationService notificationService,
                               ErsService ersService,
                               ExperimentConfig experimentConfig) {
        this.experimentRepository = experimentRepository;
        this.experimentService = experimentService;
        this.notificationService = notificationService;
        this.ersService = ersService;
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
                    if (RequestStatus.FINISHED.equals(experiment.getExperimentStatus())) {
                        ersService.sentExperimentHistory(experiment, experimentHistory,
                                ExperimentResultsRequestSource.SYSTEM);
                    }
                });
            }

            @Override
            public Page<Experiment> findNextPage(Pageable pageable) {
                return experimentRepository.findNotSentExperiments(Collections.singletonList(RequestStatus.NEW),
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
                for (Experiment experiment : experiments) {
                    try {
                        notificationService.notifyByEmail(experiment);
                        experiment.setSentDate(LocalDateTime.now());
                        experimentRepository.save(experiment);
                    } catch (Exception ex) {
                        log.error("There was an error while sending email request for experiment with id [{}]: {}",
                                experiment.getId(), ex.getMessage());
                    }
                }
            }

            @Override
            public Page<Experiment> findNextPage(Pageable pageable) {
                return experimentRepository.findNotSentExperiments(
                        Arrays.asList(RequestStatus.FINISHED, RequestStatus.ERROR, RequestStatus.TIMEOUT),
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
     * Processes experiments using pagination.
     *
     * @param pageableCallback callback {@link PageableCallback}
     */
    private <T> void processExperiments(PageableCallback<T> pageableCallback) {
        Pageable pageable = PageRequest.of(0, experimentConfig.getPageSize());
        Page<T> page;
        int totalCount = 0;
        while ((page = pageableCallback.findNextPage(pageable)).hasContent()) {
            log.trace("Obtained new experiments page [{}, {}] for processing.", page.getNumber(), page.getSize());
            pageableCallback.perform(page.getContent());
            totalCount += page.getTotalElements();
            log.trace("Experiments page [{}, {}] has been processed.", page.getNumber(), page.getSize());
            if (!page.hasNext()) {
                log.trace("All experiments has been processed");
                break;
            }
            pageable = page.nextPageable();
        }
        log.trace("{} experiments has been processed.", totalCount);
    }

}
