package com.ecaservice.server.service.evaluation;

import com.ecaservice.core.filter.query.CountQuerySimpleExecutor;
import com.ecaservice.core.filter.specification.AbstractFilter;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.web.dto.model.PageRequestDto;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import static com.ecaservice.core.filter.config.CoreFilterConfiguration.PAGE_REQUEST_KEY_GENERATOR;
import static com.ecaservice.server.config.cache.CacheNames.EVALUATION_LOGS_TOTAL_COUNT_QUERY;

/**
 * Evaluation log count query executor.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class EvaluationLogCountQueryExecutor extends CountQuerySimpleExecutor {

    public EvaluationLogCountQueryExecutor(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    @Cacheable(value = EVALUATION_LOGS_TOTAL_COUNT_QUERY, keyGenerator = PAGE_REQUEST_KEY_GENERATOR)
    public <T> long countQuery(AbstractFilter<T> filter, PageRequestDto pageRequestDto) {
        log.info("Gets evaluation logs count query for page request: {}", pageRequestDto);
        long totalCount = super.countQuery(filter, pageRequestDto);
        log.info("Evaluation logs count query result [{}] for page request: {}", totalCount, pageRequestDto);
        return totalCount;
    }
}
