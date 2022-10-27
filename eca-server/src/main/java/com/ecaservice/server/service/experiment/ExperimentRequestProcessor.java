package com.ecaservice.server.service.experiment;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.event.model.ExperimentEmailEvent;
import com.ecaservice.server.event.model.ExperimentResponseEvent;
import com.ecaservice.server.event.model.push.ExperimentWebPushEvent;
import com.ecaservice.server.model.entity.Channel;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.ExperimentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.ecaservice.common.web.util.LogHelper.EV_REQUEST_ID;
import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.common.web.util.PageHelper.processWithPagination;
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
    private final ExperimentService experimentService;
    private final ExperimentStepProcessor experimentStepProcessor;
    private final ApplicationEventPublisher eventPublisher;
    private final ExperimentProgressService experimentProgressService;
    private final ExperimentConfig experimentConfig;

    /**
     * Starts experiment.
     *
     * @param id - experiment id
     */
    @Locked(lockName = "experiment", key = "#id", lockRegistry = EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN,
            waitForLock = false)
    public void startExperiment(Long id) {
        var experiment = experimentService.getById(id);
        if (!RequestStatus.NEW.equals(experiment.getRequestStatus())) {
            log.warn("Attempt to process new experiment [{}] with status [{}]. Skipped...", experiment.getRequestId(),
                    experiment.getRequestStatus());
            return;
        }
        putMdc(TX_ID, experiment.getRequestId());
        putMdc(EV_REQUEST_ID, experiment.getRequestId());
        log.info("Starting to process new experiment [{}]", experiment.getRequestId());
        experimentProgressService.start(experiment);
        experimentService.startExperiment(experiment);
        eventPublisher.publishEvent(new ExperimentWebPushEvent(this, experiment));
        eventPublisher.publishEvent(new ExperimentEmailEvent(this, experiment));
    }

    /**
     * Processes experiment.
     *
     * @param id - experiment id
     */
    @Locked(lockName = "experiment", key = "#id", lockRegistry = EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN,
            waitForLock = false)
    public void processExperiment(Long id) {
        var experiment = experimentService.getById(id);
        if (!RequestStatus.IN_PROGRESS.equals(experiment.getRequestStatus())) {
            log.warn("Attempt to process experiment [{}] with status [{}]. Skipped...", experiment.getRequestId(),
                    experiment.getRequestStatus());
            return;
        }
        putMdc(TX_ID, experiment.getRequestId());
        putMdc(EV_REQUEST_ID, experiment.getRequestId());
        log.info("Starting to process experiment [{}]", experiment.getRequestId());
        experimentStepProcessor.processExperimentSteps(experiment);
        log.info("Experiment [{}] has been processed", experiment.getRequestId());
    }

    /**
     * Finishes experiment.
     *
     * @param id - experiment id
     */
    @Locked(lockName = "experiment", key = "#id", lockRegistry = EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN,
            waitForLock = false)
    public void finishExperiment(Long id) {
        var experiment = experimentService.getById(id);
        putMdc(TX_ID, experiment.getRequestId());
        putMdc(EV_REQUEST_ID, experiment.getRequestId());
        log.info("Starting to finish experiment [{}]", experiment.getRequestId());
        experimentService.finishExperiment(experiment);
        if (Channel.QUEUE.equals(experiment.getChannel())) {
            eventPublisher.publishEvent(new ExperimentResponseEvent(this, experiment));
        }
        eventPublisher.publishEvent(new ExperimentWebPushEvent(this, experiment));
        eventPublisher.publishEvent(new ExperimentEmailEvent(this, experiment));
        experimentProgressService.finish(experiment);
        log.info("Experiment [{}] has been finished", experiment.getRequestId());
    }

    /**
     * Removes experiments model files from disk.
     */
    @Locked(lockName = EXPERIMENTS_CRON_JOB_KEY, lockRegistry = EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN,
            waitForLock = false)
    public void removeExperimentsModels() {
        log.info("Starting to remove experiments models.");
        LocalDateTime dateTime = LocalDateTime.now().minusDays(experimentConfig.getNumberOfDaysForStorage());
        var experimentIds = experimentRepository.findExperimentsModelsToDelete(dateTime);
        log.info("Obtained {} experiments to remove model files", experimentIds.size());
        processWithPagination(experimentIds, experimentRepository::findByIdIn, this::removedExperimentsModels,
                experimentConfig.getPageSize());
        log.info("Experiments models removing has been finished.");
    }

    /**
     * Removes experiments training data files from disk.
     */
    @Locked(lockName = EXPERIMENTS_CRON_JOB_KEY, lockRegistry = EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN,
            waitForLock = false)
    public void removeExperimentsTrainingData() {
        log.info("Starting to remove experiments training data.");
        LocalDateTime dateTime = LocalDateTime.now().minusDays(experimentConfig.getNumberOfDaysForStorage());
        var experimentIds = experimentRepository.findExperimentsTrainingDataToDelete(dateTime);
        log.info("Obtained {} experiments to remove training data files", experimentIds.size());
        processWithPagination(experimentIds, experimentRepository::findByIdIn, this::removeExperimentsTrainingData,
                experimentConfig.getPageSize());
        log.info("Experiments training data removing has been finished.");
    }

    private void removedExperimentsModels(List<Experiment> experiments) {
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
    }

    private void removeExperimentsTrainingData(List<Experiment> experiments) {
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
    }
}
