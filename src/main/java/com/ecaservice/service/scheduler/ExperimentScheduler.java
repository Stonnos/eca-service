package com.ecaservice.service.scheduler;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentStatus;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.PageableCallback;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.service.experiment.mail.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
    private final ExperimentConfig experimentConfig;

    /**
     * Constructor with dependency spring injection.
     *
     * @param experimentRepository {@link ExperimentRepository} bean
     * @param experimentService    {@link ExperimentService} bean
     * @param notificationService  {@link NotificationService} bean
     * @param experimentConfig     {@link ExperimentConfig} bean
     */
    @Autowired
    public ExperimentScheduler(ExperimentRepository experimentRepository,
                               ExperimentService experimentService,
                               NotificationService notificationService,
                               ExperimentConfig experimentConfig) {
        this.experimentRepository = experimentRepository;
        this.experimentService = experimentService;
        this.notificationService = notificationService;
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
                for (Experiment experiment : experiments) {
                    experimentService.processExperiment(experiment);
                }
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
                for (Experiment experiment : experiments) {
                    notificationService.notifyByEmail(experiment);
                }
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
    @Scheduled(cron = "${experiment.schedulerCron}")
    public void processRequestsToRemove() {
        log.info("Starting to remove experiments data.");
        final LocalDateTime dateTime = LocalDateTime.now().minusDays(experimentConfig.getNumberOfDaysForStorage());
        processExperiments(new PageableCallback<Experiment>() {
            @Override
            public void perform(List<Experiment> experiments) {
                for (Experiment experiment : experiments) {
                    experimentService.removeExperimentData(experiment);
                }
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
     * @param pageableCallback {@link PageableCallback} object
     */
    private void processExperiments(PageableCallback<Experiment> pageableCallback) {
        Pageable pageable = new PageRequest(0, experimentConfig.getPageSize());
        Page<Experiment> page;
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
