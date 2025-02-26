package com.ecaservice.server.service.experiment;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.repository.ExperimentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;

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
    @Locked(lockName = EXPERIMENTS_CRON_JOB_KEY, waitForLock = false)
    public void removeExperimentsModels() {
        log.info("Starting to remove experiments models.");
        if (!appProperties.isAutoDeleteExpiredModels()) {
            log.warn("Experiment model auto removing has been disabled. Skipped...");
            return;
        }
        Pageable pageRequest = PageRequest.of(0, appProperties.getPageSize());
        LocalDateTime dateTime = LocalDateTime.now().minusDays(appProperties.getNumberOfDaysForStorage());
        List<Experiment> experiments;
        while (!(experiments = experimentRepository.findExperimentsModelsToDelete(dateTime, LocalDateTime.now(),
                pageRequest)).isEmpty()) {
            experiments.forEach(this::removeModel);
        }
        log.info("Experiments models removing has been finished.");
    }

    private void removeModel(Experiment experiment) {
        putMdc(TX_ID, experiment.getRequestId());
        experimentDataService.removeExperimentModel(experiment);
    }
}
