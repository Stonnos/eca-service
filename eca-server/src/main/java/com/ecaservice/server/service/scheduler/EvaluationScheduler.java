package com.ecaservice.server.service.scheduler;

import com.ecaservice.server.config.ClassifiersProperties;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.evaluation.ClassifiersDataCleaner;
import com.ecaservice.server.service.evaluation.EvaluationProcessManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
    private final EvaluationLogRepository evaluationLogRepository;

    /**
     * Processes new evaluation requests.
     */
    @Scheduled(fixedDelayString = "${classifiers.delaySeconds}000")
    public void processEvaluationRequests() {
        log.trace("Starting to process new evaluation requests.");
        var pageRequest = PageRequest.of(0, classifiersProperties.getMaxRequestsPerJob());
        var ids = evaluationLogRepository.findNewWebRequests(pageRequest);
        if (!CollectionUtils.isEmpty(ids)) {
            log.info("Fetched new [{}] evaluation requests to process", ids.size());
            ids.forEach(evaluationProcessManager::processEvaluationRequest);
        }
        log.trace("New evaluation request processing has been successfully finished.");
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

}
