package com.ecaservice.service.scheduler;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.event.model.ExperimentChangeStatusEvent;
import com.ecaservice.event.model.ExperimentFinishedEvent;
import com.ecaservice.model.entity.AppInstanceEntity;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.repository.ExperimentResultsEntityRepository;
import com.ecaservice.service.AppInstanceService;
import com.ecaservice.service.ers.ErsService;
import com.ecaservice.service.experiment.ExperimentProgressService;
import com.ecaservice.service.experiment.ExperimentService;
import eca.converters.model.ExperimentHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    private final ExperimentRepository experimentRepository;
    private final ExperimentResultsEntityRepository experimentResultsEntityRepository;
    private final ExperimentService experimentService;
    private final ApplicationEventPublisher eventPublisher;
    private final ErsService ersService;
    private final AppInstanceService appInstanceService;
    private final ExperimentProgressService experimentProgressService;
    private final ExperimentConfig experimentConfig;

    /**
     * Processing new experiment requests.
     */
    @Scheduled(fixedDelayString = "${experiment.delaySeconds}000")
    public void processNewRequests() {
        log.trace("Starting to process new experiments.");
        AppInstanceEntity appInstanceEntity = appInstanceService.getAppInstanceEntity();
        List<Experiment> experiments = experimentRepository.findExperimentsForProcessing(appInstanceEntity,
                Collections.singletonList(RequestStatus.NEW));
        log.trace("Obtained {} new experiments", experiments.size());
        experiments.forEach(experiment -> {
            putMdc(TX_ID, experiment.getRequestId());
            putMdc(EV_REQUEST_ID, experiment.getRequestId());
            experimentProgressService.start(experiment);
            setInProgressStatus(experiment);
            ExperimentHistory experimentHistory = experimentService.processExperiment(experiment);
            eventPublisher.publishEvent(new ExperimentChangeStatusEvent(this, experiment));
            if (RequestStatus.FINISHED.equals(experiment.getRequestStatus())) {
                eventPublisher.publishEvent(new ExperimentFinishedEvent(this, experiment, experimentHistory));
            }
            experimentProgressService.finish(experiment);
        });
        log.trace("New experiments processing has been successfully finished.");
    }

    /**
     * Processing experiment results sending to emails.
     */
    @Scheduled(fixedDelayString = "${experiment.delaySeconds}000")
    public void processRequestsToSent() {
        log.trace("Starting to sent experiment results.");
        AppInstanceEntity appInstanceEntity = appInstanceService.getAppInstanceEntity();
        List<Experiment> experiments = experimentRepository.findExperimentsForProcessing(appInstanceEntity,
                Arrays.asList(RequestStatus.FINISHED, RequestStatus.ERROR, RequestStatus.TIMEOUT));
        log.trace("Obtained {} experiments to sent results", experiments.size());
        for (Experiment experiment : experiments) {
            putMdc(TX_ID, experiment.getRequestId());
            putMdc(EV_REQUEST_ID, experiment.getRequestId());
            eventPublisher.publishEvent(new ExperimentChangeStatusEvent(this, experiment));
        }
        log.trace("Sending experiments has been successfully finished.");
    }

    /**
     * Try to sent experiments results to ERS service.
     */
    @Scheduled(cron = "${experiment.ersSendingCron}")
    public void processRequestsToErs() {
        log.info("Starting to sent experiment results to ERS service");
        AppInstanceEntity appInstanceEntity = appInstanceService.getAppInstanceEntity();
        List<ExperimentResultsEntity> experimentResultsEntities =
                experimentResultsEntityRepository.findExperimentsResultsToErsSent(appInstanceEntity);
        log.trace("Obtained {} experiments results sending to ERS service", experimentResultsEntities.size());
        Map<Experiment, List<ExperimentResultsEntity>> experimentResultsMap =
                experimentResultsEntities.stream().collect(
                        Collectors.groupingBy(ExperimentResultsEntity::getExperiment));
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
        log.info("Finished to sent experiment results to ERS service");
    }

    /**
     * Removes experiments data files from disk. Schedules by cron.
     */
    @Scheduled(cron = "${experiment.removeExperimentCron}")
    public void processRequestsToRemove() {
        log.info("Starting to remove experiments data.");
        AppInstanceEntity appInstanceEntity = appInstanceService.getAppInstanceEntity();
        LocalDateTime dateTime = LocalDateTime.now().minusDays(experimentConfig.getNumberOfDaysForStorage());
        List<Experiment> experiments = experimentRepository.findNotDeletedExperiments(appInstanceEntity, dateTime);
        log.trace("Obtained {} experiments to remove data", experiments.size());
        experiments.forEach(experiment -> {
            putMdc(TX_ID, experiment.getRequestId());
            putMdc(EV_REQUEST_ID, experiment.getRequestId());
            experimentService.removeExperimentData(experiment);
        });
        log.info("Experiments data removing has been finished.");
    }

    private void setInProgressStatus(Experiment experiment) {
        experiment.setRequestStatus(RequestStatus.IN_PROGRESS);
        experiment.setStartDate(LocalDateTime.now());
        experimentRepository.save(experiment);
        log.info("Experiment [{}] in progress status has been set", experiment.getRequestId());
        eventPublisher.publishEvent(new ExperimentChangeStatusEvent(this, experiment));
    }
}
