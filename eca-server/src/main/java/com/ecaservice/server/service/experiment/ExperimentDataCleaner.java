package com.ecaservice.server.service.experiment;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.repository.ExperimentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

import static com.ecaservice.common.web.util.LogHelper.EV_REQUEST_ID;
import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.common.web.util.PageHelper.processWithPagination;
import static com.ecaservice.server.config.EcaServiceConfiguration.EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN;

/**
 * Experiment data cleaner.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentDataCleaner {

    private static final String EXPERIMENTS_CRON_JOB_KEY = "experimentsCronJob";

    private final ExperimentRepository experimentRepository;
    private final ExperimentDataService experimentDataService;
    private final AppProperties appProperties;

    /**
     * Removes experiments model files from S3.
     */
    @Locked(lockName = EXPERIMENTS_CRON_JOB_KEY, lockRegistry = EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN,
            waitForLock = false)
    public void removeExperimentsModels() {
        log.info("Starting to remove experiments models.");
        LocalDateTime dateTime = LocalDateTime.now().minusDays(appProperties.getNumberOfDaysForStorage());
        var experimentIds = experimentRepository.findExperimentsModelsToDelete(dateTime);
        log.info("Obtained {} experiments to remove model files", experimentIds.size());
        processWithPagination(experimentIds, experimentRepository::findByIdIn,
                experiments -> removeData(experiments, experimentDataService::removeExperimentModel),
                appProperties.getPageSize()
        );
        log.info("Experiments models removing has been finished.");
    }

    private void removeData(List<Experiment> experiments, Consumer<Experiment> removeDataConsumer) {
        experiments.forEach(experiment -> {
            putMdc(TX_ID, experiment.getRequestId());
            putMdc(EV_REQUEST_ID, experiment.getRequestId());
            removeDataConsumer.accept(experiment);
        });
    }
}
