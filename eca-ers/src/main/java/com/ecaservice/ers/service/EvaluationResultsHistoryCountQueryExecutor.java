package com.ecaservice.ers.service;

import com.ecaservice.core.filter.query.CountQuerySimpleExecutor;
import com.ecaservice.core.filter.specification.AbstractFilter;
import com.ecaservice.web.dto.model.PageRequestDto;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import static com.ecaservice.core.filter.config.CoreFilterConfiguration.PAGE_REQUEST_KEY_GENERATOR;
import static com.ecaservice.ers.config.CacheNames.EVALUATION_RESULTS_HISTORY_COUNT_QUERY;

/**
 * Evaluation results history count query executor.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class EvaluationResultsHistoryCountQueryExecutor extends CountQuerySimpleExecutor {

    public EvaluationResultsHistoryCountQueryExecutor(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    @Cacheable(value = EVALUATION_RESULTS_HISTORY_COUNT_QUERY, keyGenerator = PAGE_REQUEST_KEY_GENERATOR)
    public <T> long countQuery(AbstractFilter<T> filter, PageRequestDto pageRequestDto) {
        log.info("Gets evaluation results history count query for page request: {}", pageRequestDto);
        long totalCount = super.countQuery(filter, pageRequestDto);
        log.info("Evaluation results history count query result [{}] for page request: {}", totalCount, pageRequestDto);
        return totalCount;
    }
}
