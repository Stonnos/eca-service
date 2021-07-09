package com.ecaservice.service.experiment;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.core.lock.annotation.TryLocked;
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
import eca.converters.model.ExperimentHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ecaservice.common.web.util.LogHelper.EV_REQUEST_ID;
import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.config.EcaServiceConfiguration.EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN;

/**
 * Experiment request processor.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentRequestProcessor {

    private static final String EXPERIMENTS_CRON_JOB_KEY = "experimentsCronJob";

    private final ExperimentRepository experimentRepository;
    private final ExperimentResultsEntityRepository experimentResultsEntityRepository;
    private final ExperimentService experimentService;
    private final ApplicationEventPublisher eventPublisher;
    private final ErsService ersService;
    private final ExperimentProgressService experimentProgressService;
    private final ExperimentConfig experimentConfig;

    /**
     * Processes new experiment.
     *
     * @param experiment - experiment entity
     */
    @TryLocked(lockName = "experiment", key = "#experiment.requestId",
            lockRegistry = EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN)
    public void processNewExperiment(Experiment experiment) {
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

    /**
     * Send email notification for finished experiment.
     *
     * @param experiment - experiment entity
     */
    @TryLocked(lockName = "experiment", key = "#experiment.requestId",
            lockRegistry = EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN)
    public void notifyExperimentFinished(Experiment experiment) {
        putMdc(TX_ID, experiment.getRequestId());
        putMdc(EV_REQUEST_ID, experiment.getRequestId());
        eventPublisher.publishEvent(new ExperimentEmailEvent(this, experiment));
    }

    /**
     * Sent experiments results to ERS service.
     */
    @TryLocked(lockName = EXPERIMENTS_CRON_JOB_KEY, lockRegistry = EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN)
    public void sentExperimentResultsToErs() {
        log.info("Starting to sent experiment results to ERS service");
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
        log.info("Finished to sent experiment results to ERS service");
    }

    /**
     * Removes experiments data files from disk.
     */
    @TryLocked(lockName = EXPERIMENTS_CRON_JOB_KEY, lockRegistry = EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN)
    public void removeExperimentsData() {
        log.info("Starting to remove experiments data.");
        LocalDateTime dateTime = LocalDateTime.now().minusDays(experimentConfig.getNumberOfDaysForStorage());
        List<Experiment> experiments = experimentRepository.findNotDeletedExperiments(dateTime);
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
        eventPublisher.publishEvent(new ExperimentWebPushEvent(this, experiment));
        eventPublisher.publishEvent(new ExperimentEmailEvent(this, experiment));
    }
}
