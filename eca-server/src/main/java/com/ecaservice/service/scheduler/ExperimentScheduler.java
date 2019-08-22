package com.ecaservice.service.scheduler;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.repository.ExperimentResultsEntityRepository;
import com.ecaservice.service.ers.ErsService;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.service.experiment.mail.NotificationService;
import eca.converters.model.ExperimentHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Experiment scheduler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ExperimentScheduler {

    private final ExperimentRepository experimentRepository;
    private final ExperimentResultsEntityRepository experimentResultsEntityRepository;
    private final ExperimentService experimentService;
    private final NotificationService notificationService;
    private final ErsService ersService;
    private final ExperimentConfig experimentConfig;

    /**
     * Constructor with dependency spring injection.
     *
     * @param experimentRepository              - experiment repository bean
     * @param experimentResultsEntityRepository - experiment results entity repository bean
     * @param experimentService                 - experiment service bean
     * @param notificationService               - notification service bean
     * @param ersService                        - ers service bean
     * @param experimentConfig                  - experiment config bean
     */
    @Inject
    public ExperimentScheduler(ExperimentRepository experimentRepository,
                               ExperimentResultsEntityRepository experimentResultsEntityRepository,
                               ExperimentService experimentService,
                               NotificationService notificationService,
                               ErsService ersService,
                               ExperimentConfig experimentConfig) {
        this.experimentRepository = experimentRepository;
        this.experimentResultsEntityRepository = experimentResultsEntityRepository;
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
        log.trace("Starting to process new experiments.");
        List<Experiment> experiments =
                experimentRepository.findNotSentExperiments(Collections.singletonList(RequestStatus.NEW));
        log.trace("Obtained {} new experiments", experiments.size());
        experiments.forEach(experiment -> {
            ExperimentHistory experimentHistory = experimentService.processExperiment(experiment);
            if (RequestStatus.FINISHED.equals(experiment.getExperimentStatus())) {
                ersService.sentExperimentHistory(experiment, experimentHistory,
                        ExperimentResultsRequestSource.SYSTEM);
            }
        });
        log.trace("New experiments processing has been successfully finished.");
    }

    /**
     * Processing experiment requests for sending.
     */
    @Scheduled(fixedDelayString = "${experiment.delaySeconds}000")
    public void processRequestsToSent() {
        log.trace("Starting to sent experiment results.");
        List<Experiment> experiments = experimentRepository.findNotSentExperiments(
                Arrays.asList(RequestStatus.FINISHED, RequestStatus.ERROR, RequestStatus.TIMEOUT));
        log.trace("Obtained {} experiments to sent results", experiments.size());
        for (Experiment experiment : experiments) {
            try {
                notificationService.notifyByEmail(experiment);
                experiment.setSentDate(LocalDateTime.now());
                experimentRepository.save(experiment);
            } catch (Exception ex) {
                log.error("There was an error while sending email request for experiment [{}]: {}",
                        experiment.getUuid(), ex.getMessage());
            }
        }
        log.trace("Sending experiments has been successfully finished.");
    }

    /**
     * Try to sent experiments results to ERS service.
     */
    @Scheduled(cron = "${experiment.ersSendingCron}")
    public void processRequestsToErs() {
        log.info("Starting to sent experiment results to ERS service");
       /* List<Experiment> experiments = experimentRepository.findExperimentsToErsSent();
        log.trace("Obtained {} experiments sending to ERS service", experiments.size());
        experiments.forEach(experiment -> {
            try {
                ExperimentHistory experimentHistory = experimentService.getExperimentResults(experiment);
                ersService.sentExperimentHistory(experiment, experimentHistory,
                        ExperimentResultsRequestSource.SYSTEM);
            } catch (Exception ex) {
                log.error("There was an error while sending experiment [{}] history: {}", experiment.getUuid(),
                        ex.getMessage());
            }
        });*/
        List<ExperimentResultsEntity> experimentResultsEntities =
                experimentResultsEntityRepository.findfindExperimentsResulsToErsSent();
        log.trace("Obtained {} experiments results sending to ERS service", experimentResultsEntities.size());
        Map<Experiment, List<ExperimentResultsEntity>> experimentResultsMap =
                experimentResultsEntities.stream().collect(
                        Collectors.groupingBy(ExperimentResultsEntity::getExperiment));
        experimentResultsMap.forEach((experiment, experimentResultsEntityList) -> {
            try {
                ExperimentHistory experimentHistory = experimentService.getExperimentResults(experiment);
                experimentResultsEntityList.forEach(
                        experimentResultsEntity -> ersService.sentExperimentResults(experimentResultsEntity,
                                experimentHistory, ExperimentResultsRequestSource.SYSTEM));
            } catch (Exception ex) {
                log.error("There was an error while sending experiment [{}] history: {}", experiment.getUuid(),
                        ex.getMessage());
            }
        });
        log.info("Finished to sent experiment results to ERS service");
    }

    /**
     * Removes experiments data files from disk. Schedules by cron.
     */
    @Scheduled(cron = "${experiment.removeExperimentCron}")
    public void processRequestsToRemove() {
        log.info("Starting to remove experiments data.");
        LocalDateTime dateTime = LocalDateTime.now().minusDays(experimentConfig.getNumberOfDaysForStorage());
        List<Experiment> experiments = experimentRepository.findNotDeletedExperiments(dateTime);
        log.trace("Obtained {} experiments to remove data", experiments.size());
        experiments.forEach(experimentService::removeExperimentData);
        log.info("Experiments data removing has been finished.");
    }
}
