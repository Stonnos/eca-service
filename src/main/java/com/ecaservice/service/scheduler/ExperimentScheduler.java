package com.ecaservice.service.scheduler;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.service.experiment.mail.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
     * @param experimentRepository {@link ExperimentRepository} bean
     * @param experimentService    {@link ExperimentService} bean
     * @param notificationService  {@link NotificationService} bean
     * @param experimentConfig {@link ExperimentConfig} bean
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
    @Scheduled(fixedDelayString = "${experiment.delay}")
    public void processingNewRequests() {
        log.trace("Starting to built experiments.");
        List<Experiment> experiments =
                experimentRepository.findByExperimentStatusInAndSentDateIsNull(experimentConfig.getProcessStatuses());
        log.trace("{} new experiments has been obtained.", experiments.size());
        for (Experiment experiment : experiments) {
            experimentService.processExperiment(experiment);
        }
        log.trace("Building experiments has been successfully finished.");
    }

    /**
     * Processing experiment requests for sending.
     */
    @Scheduled(fixedDelayString = "${experiment.delay}")
    public void processingRequestsToSent() {
        log.trace("Starting to sent experiment results.");
        List<Experiment> experiments =
                experimentRepository.findByExperimentStatusInAndSentDateIsNull(experimentConfig.getSentStatuses());
        log.trace("{} experiments has been obtained for sending.", experiments.size());
        for (Experiment experiment : experiments) {
            notificationService.notifyByEmail(experiment);
        }
        log.trace("Sending experiments has been successfully finished.");
    }
}
