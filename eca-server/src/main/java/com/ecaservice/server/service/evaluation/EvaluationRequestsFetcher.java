package com.ecaservice.server.service.evaluation;

import com.ecaservice.server.config.ClassifiersProperties;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.repository.EvaluationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<EvaluationLog> lockNextEvaluationRequestsToProcess(Pageable pageable) {
        log.debug("Starting to get next evaluations to process");
        List<EvaluationLog> evaluationLogs =
                evaluationLogRepository.findRequestsToProcess(LocalDateTime.now(), pageable);
        if (!CollectionUtils.isEmpty(evaluationLogs)) {
            // Sets a lock to prevent other threads from receiving the same data for processing
            LocalDateTime lockedTtl = LocalDateTime.now().plusSeconds(classifiersProperties.getLockTtlSeconds());
            var ids = evaluationLogs.stream()
                    .map(EvaluationLog::getId)
                    .toList();
            evaluationLogRepository.lock(ids, lockedTtl);
            log.info("[{}] evaluation requests to process has been fetched", evaluationLogs.size());
        }
        log.debug("Next evaluations to process has been fetched");
        return evaluationLogs;
    }
}
