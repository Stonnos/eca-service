package com.ecaservice.server.service.scheduler;

import com.ecaservice.server.config.ClassifiersProperties;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.evaluation.ClassifiersDataCleaner;
import com.ecaservice.server.service.evaluation.EvaluationProcessManager;
import com.ecaservice.server.service.evaluation.EvaluationRequestsFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.ecaservice.server.util.PageHelper.processWithPagination;

/**
 * Evaluation scheduler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationScheduler {

    private final ClassifiersProperties classifiersProperties;
    private final EvaluationProcessManager evaluationProcessManager;
    private final ClassifiersDataCleaner classifiersDataCleaner;
    private final EvaluationRequestsFetcher evaluationRequestsFetcher;
    private final EvaluationLogRepository evaluationLogRepository;

    /**
     * Processes new evaluation requests.
     */
    @Scheduled(fixedDelayString = "${classifiers.delaySeconds}000")
    public void processEvaluationRequests() {
        log.debug("Starting to process new evaluation requests.");
        processWithPagination(evaluationRequestsFetcher::getNextEvaluationRequestsToProcess,
                this::processEvaluationRequests,
                classifiersProperties.getBatchSize()
        );
        log.debug("New evaluation request processing has been successfully finished.");
    }

    /**
     * Removes evaluation data files from S3. Schedules by cron.
     */
    @Scheduled(cron = "${app.removeModelCron}")
    public void processRequestsToRemove() {
        log.info("Starting job to removes evaluation data files from disk");
        classifiersDataCleaner.removeModels();
        log.info("Removing evaluation data files job has been finished");
    }

    private void processEvaluationRequests(List<EvaluationLog> evaluationLogs) {
        evaluationLogs.forEach(evaluationLog -> {
            // Renews locked ttl for evaluation log
            evaluationLog.setLockedTtl(LocalDateTime.now().plusSeconds(classifiersProperties.getLockTtlSeconds()));
            evaluationLogRepository.save(evaluationLog);
            evaluationProcessManager.processEvaluationRequest(evaluationLog);
        });
    }
}
