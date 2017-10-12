package com.ecaservice.service.scheduler;

import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentStatus;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.service.experiment.mail.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Experiment scheduler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ExperimentScheduler {

    private static final Collection<ExperimentStatus> SENT_STATUSES =
            Arrays.asList(ExperimentStatus.FINISHED, ExperimentStatus.ERROR, ExperimentStatus.TIMEOUT);

    private final ExperimentRepository experimentRepository;
    private final ExperimentService experimentService;
    private final NotificationService notificationService;

    /**
     * Constructor with dependency spring injection.
     *
     * @param experimentRepository {@link ExperimentRepository} bean
     * @param experimentService    {@link ExperimentService} bean
     * @param notificationService  {@link NotificationService} bean
     */
    @Autowired
    public ExperimentScheduler(ExperimentRepository experimentRepository,
                               ExperimentService experimentService,
                               NotificationService notificationService) {
        this.experimentRepository = experimentRepository;
        this.experimentService = experimentService;
        this.notificationService = notificationService;
    }

    /**
     * Processing new experiment requests.
     */
    @Scheduled(fixedDelayString = "${experiment.delay}")
    public void processingNewRequests() {
        log.info("Starting to built experiments.");
        List<Experiment> experiments =
                experimentRepository.findByExperimentStatusInAndSentDateIsNull(Arrays.asList(ExperimentStatus.NEW));
        log.info("{} new experiments has been obtained.", experiments.size());
        for (Experiment experiment : experiments) {
            experimentService.processExperiment(experiment);
        }
        log.info("Building experiments has been successfully finished.");
    }

    /**
     * Processing experiment requests for sending.
     */
    @Scheduled(fixedDelayString = "${experiment.delay}")
    public void processingRequestsToSent() {
        log.info("Starting to sent experiment results.");
        List<Experiment> experiments =
                experimentRepository.findByExperimentStatusInAndSentDateIsNull(SENT_STATUSES);
        log.info("{} experiments has been obtained for sending.", experiments.size());
        for (Experiment experiment : experiments) {
            notificationService.notifyByEmail(experiment);
        }
        log.info("Sending experiments has been successfully finished.");
    }
}
