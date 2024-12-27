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

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

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
    private final ExecutorService evaluationRequestExecutorService;
    private final EvaluationLogRepository evaluationLogRepository;

    /**
     * Processes evaluation requests.
     */
    @Scheduled(fixedDelayString = "${classifiers.delaySeconds}000")
    public void processEvaluationRequests() {
        log.debug("Starting to process evaluation requests.");
        processWithPagination(evaluationRequestsFetcher::getNextEvaluationRequestsToProcess,
                this::processEvaluationRequests,
                classifiersProperties.getBatchSize()
        );
        log.debug("Evaluation request processing has been successfully finished.");
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
        List<CompletableFuture<Void>> futures = evaluationLogs.stream()
                .map(evaluationLog -> {
                    Runnable task = () -> {
                        evaluationProcessManager.processEvaluationRequest(evaluationLog);
                        evaluationLogRepository.resetLock(evaluationLog.getId());
                    };
                    return CompletableFuture.runAsync(task, evaluationRequestExecutorService);
                }).toList();
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.join();
    }
}
