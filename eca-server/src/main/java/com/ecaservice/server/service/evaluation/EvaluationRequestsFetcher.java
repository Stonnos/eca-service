package com.ecaservice.server.service.evaluation;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.server.config.ClassifiersProperties;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.repository.EvaluationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Evaluation requests fetcher.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationRequestsFetcher {

    private final ClassifiersProperties classifiersProperties;
    private final EvaluationLogRepository evaluationLogRepository;

    /**
     * Gets next evaluation requests for processing and sets a lock to prevent other threads from receiving the same
     * data for processing.
     *
     * @param pageable - pageable object
     * @return evaluations page
     */
    @Locked(lockName = "getNextEvaluationRequestsToProcess")
    public Page<EvaluationLog> getNextEvaluationRequestsToProcess(Pageable pageable) {
        log.debug("Starting to get next evaluations to process");
        Page<EvaluationLog> evaluationLogs = evaluationLogRepository.findRequestsToProcess(LocalDateTime.now(), pageable);
        if (evaluationLogs.hasContent()) {
            // Sets a lock to prevent other threads from receiving the same data for processing
            evaluationLogs.forEach(evaluationLog -> evaluationLog.setLockedTtl(
                    LocalDateTime.now().plusSeconds(classifiersProperties.getLockTtlSeconds())));
            evaluationLogRepository.saveAll(evaluationLogs);
            log.info("[{}] evaluation requests to process has been fetched", evaluationLogs.getNumberOfElements());
        }
        log.debug("Next evaluations to process has been fetched");
        return evaluationLogs;
    }
}
