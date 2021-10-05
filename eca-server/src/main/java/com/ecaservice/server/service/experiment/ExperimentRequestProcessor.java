package com.ecaservice.server.service.experiment;

import com.ecaservice.core.lock.annotation.TryLocked;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.event.model.ExperimentEmailEvent;
import com.ecaservice.server.event.model.ExperimentFinishedEvent;
import com.ecaservice.server.event.model.ExperimentResponseEvent;
import com.ecaservice.server.event.model.ExperimentWebPushEvent;
import com.ecaservice.server.model.entity.Channel;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentResultsEntity;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.repository.ExperimentResultsEntityRepository;
import com.ecaservice.server.service.ers.ErsService;
import eca.dataminer.AbstractExperiment;
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
import static com.ecaservice.server.config.EcaServiceConfiguration.EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN;

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
        log.info("Starting to process new experiment [{}]", experiment.getRequestId());
        experimentProgressService.start(experiment);
        setInProgressStatus(experiment);
        AbstractExperiment<?> experimentHistory = experimentService.processExperiment(experiment);
        if (Channel.QUEUE.equals(experiment.getChannel())) {
            eventPublisher.publishEvent(new ExperimentResponseEvent(this, experiment));
        }
        eventPublisher.publishEvent(new ExperimentWebPushEvent(this, experiment));
        eventPublisher.publishEvent(new ExperimentEmailEvent(this, experiment));
        if (RequestStatus.FINISHED.equals(experiment.getRequestStatus())) {
            eventPublisher.publishEvent(new ExperimentFinishedEvent(this, experiment, experimentHistory));
        }
        experimentProgressService.finish(experiment);
        log.info("New experiment [{}] has been processed", experiment.getRequestId());
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
        log.info("Retry to sent email for finished experiment [{}]", experiment.getRequestId());
        eventPublisher.publishEvent(new ExperimentEmailEvent(this, experiment));
        log.info("Email sent retrying process has been finished for finished experiment [{}]",
                experiment.getRequestId());
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
                AbstractExperiment<?> abstractExperiment = experimentService.getExperimentHistory(experiment);
                experimentResultsEntityList.forEach(
                        experimentResultsEntity -> ersService.sentExperimentResults(experimentResultsEntity,
                                abstractExperiment, ExperimentResultsRequestSource.SYSTEM));
            } catch (Exception ex) {
                log.error("There was an error while sending experiment [{}] history: {}", experiment.getRequestId(),
                        ex.getMessage());
            }
        });
        log.info("Finished to sent experiment results to ERS service");
    }

    /**
     * Removes experiments model files from disk.
     */
    @TryLocked(lockName = EXPERIMENTS_CRON_JOB_KEY, lockRegistry = EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN)
    public void removeExperimentsModels() {
        log.info("Starting to remove experiments models.");
        LocalDateTime dateTime = LocalDateTime.now().minusDays(experimentConfig.getNumberOfDaysForStorage());
        List<Experiment> experiments = experimentRepository.findExperimentsModelsToDelete(dateTime);
        log.info("Obtained {} experiments to remove model files", experiments.size());
        experiments.forEach(experiment -> {
            putMdc(TX_ID, experiment.getRequestId());
            putMdc(EV_REQUEST_ID, experiment.getRequestId());
            try {
                experimentService.removeExperimentModel(experiment);
            } catch (Exception ex) {
                log.error("There was an error while remove experiment [{}] model file: {}", experiment.getRequestId(),
                        ex.getMessage());
            }
        });
        log.info("Experiments models removing has been finished.");
    }

    /**
     * Removes experiments training data files from disk.
     */
    @TryLocked(lockName = EXPERIMENTS_CRON_JOB_KEY, lockRegistry = EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN)
    public void removeExperimentsTrainingData() {
        log.info("Starting to remove experiments training data.");
        LocalDateTime dateTime = LocalDateTime.now().minusDays(experimentConfig.getNumberOfDaysForStorage());
        List<Experiment> experiments = experimentRepository.findExperimentsTrainingDataToDelete(dateTime);
        log.info("Obtained {} experiments to remove training data files", experiments.size());
        experiments.forEach(experiment -> {
            putMdc(TX_ID, experiment.getRequestId());
            putMdc(EV_REQUEST_ID, experiment.getRequestId());
            try {
                experimentService.removeExperimentTrainingData(experiment);
            } catch (Exception ex) {
                log.error("There was an error while remove experiment [{}] training data file: {}",
                        experiment.getRequestId(), ex.getMessage());
            }
        });
        log.info("Experiments training data removing has been finished.");
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