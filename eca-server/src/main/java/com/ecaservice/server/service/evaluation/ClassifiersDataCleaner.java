package com.ecaservice.server.service.evaluation;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.evaluation.EvaluationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.ecaservice.common.web.util.LogHelper.EV_REQUEST_ID;
import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.common.web.util.PageHelper.processWithPagination;
import static com.ecaservice.server.config.EcaServiceConfiguration.EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN;

/**
 * Classifiers data cleaner.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifiersDataCleaner {

    private static final String CLASSIFIERS_CRON_JOB_KEY = "classifiersCronJob";

    private final EvaluationLogRepository evaluationLogRepository;
    private final EvaluationLogService evaluationLogService;
    private final AppProperties appProperties;

    /**
     * Removes experiments model files from S3.
     */
    @Locked(lockName = CLASSIFIERS_CRON_JOB_KEY, lockRegistry = EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN,
            waitForLock = false)
    public void removeModels() {
        log.info("Starting to remove classifier models.");
        LocalDateTime dateTime = LocalDateTime.now().minusDays(appProperties.getNumberOfDaysForStorage());
        var ids = evaluationLogRepository.findModelsToDelete(dateTime);
        log.info("Obtained {} classifiers to remove model files", ids.size());
        processWithPagination(ids, evaluationLogRepository::findByIdIn,
                evaluationLogs -> evaluationLogs.forEach(this::removeModel),
                appProperties.getPageSize()
        );
        log.info("Classifier models removing has been finished.");
    }

    private void removeModel(EvaluationLog evaluationLog) {
        putMdc(TX_ID, evaluationLog.getRequestId());
        putMdc(EV_REQUEST_ID, evaluationLog.getRequestId());
        evaluationLogService.removeModel(evaluationLog);
    }
}
