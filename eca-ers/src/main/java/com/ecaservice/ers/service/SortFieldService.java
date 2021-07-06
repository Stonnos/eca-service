package com.ecaservice.ers.service;

import com.ecaservice.ers.model.EvaluationResultsSortEntity;
import com.ecaservice.ers.repository.EvaluationResultsSortRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.ecaservice.ers.config.CacheNames.EVALUATION_RESULTS_SORT_FIELDS;

/**
 * Sort field service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SortFieldService {

    private final EvaluationResultsSortRepository evaluationResultsSortRepository;

    /**
     * Gets default evaluation results sort fields.
     *
     * @return evaluation results sort fields
     */
    @Cacheable(EVALUATION_RESULTS_SORT_FIELDS)
    public Sort getEvaluationResultsDefaultSort() {
        List<EvaluationResultsSortEntity> evaluationResultsSortEntityList =
                evaluationResultsSortRepository.findByOrderByFieldOrder();
        if (CollectionUtils.isEmpty(evaluationResultsSortEntityList)) {
            throw new IllegalStateException("Expected at least one evaluation results sort fields!");
        }
        log.info("[{}] evaluation results sort fields has been fetched", evaluationResultsSortEntityList.size());
        Sort.Order[] orders = evaluationResultsSortEntityList
                .stream()
                .map(evaluationResultsSortEntity -> evaluationResultsSortEntity.isAscending() ?
                        Sort.Order.asc(evaluationResultsSortEntity.getFieldName()) :
                        Sort.Order.desc(evaluationResultsSortEntity.getFieldName()))
                .toArray(Sort.Order[]::new);
        return Sort.by(orders);
    }
}
