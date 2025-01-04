package com.ecaservice.server.service.evaluation;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.repository.EvaluationLogRepository;
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
    private final EvaluationLogDataService evaluationLogDataService;
    private final AppProperties appProperties;

    /**
     * Removes experiments model files from S3.
     */
    @Locked(lockName = CLASSIFIERS_CRON_JOB_KEY, waitForLock = false)
    public void removeModels() {
        log.info("Starting to remove classifier models.");
        Pageable pageRequest = PageRequest.of(0, appProperties.getPageSize());
        LocalDateTime dateTime = LocalDateTime.now().minusDays(appProperties.getNumberOfDaysForStorage());
        List<EvaluationLog> evaluationLogs;
        while (!(evaluationLogs =
                evaluationLogRepository.findModelsToDelete(dateTime, LocalDateTime.now(), pageRequest)).isEmpty()) {
            evaluationLogs.forEach(this::removeModel);
        }
        log.info("Classifier models removing has been finished.");
    }

    private void removeModel(EvaluationLog evaluationLog) {
        putMdc(TX_ID, evaluationLog.getRequestId());
        evaluationLogDataService.removeModel(evaluationLog);
    }
}
