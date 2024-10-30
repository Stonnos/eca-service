package com.ecaservice.ers.service;

import com.ecaservice.ers.config.ErsConfig;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.EvaluationResultsStatisticsField;
import com.ecaservice.ers.dto.EvaluationResultsStatisticsSortField;
import com.ecaservice.ers.dto.SortDirection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * Sort field service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SortFieldService {

    private static final Map<EvaluationResultsStatisticsField, String> STATISTICS_FIELD_MAP = Map.of(
            EvaluationResultsStatisticsField.PCT_CORRECT, "statistics.pctCorrect",
            EvaluationResultsStatisticsField.MAX_AUC_VALUE, "statistics.maxAucValue",
            EvaluationResultsStatisticsField.VARIANCE_ERROR, "statistics.varianceError"
    );

    private final ErsConfig ersConfig;

    /**
     * Build sort fields for classifier options request.
     *
     * @param classifierOptionsRequest - classifier options request
     * @return sort object
     */
    public Sort buildSort(ClassifierOptionsRequest classifierOptionsRequest) {
        var evaluationResultsSortFields = getSortFields(classifierOptionsRequest);
        Sort.Order[] orders = evaluationResultsSortFields
                .stream()
                .map(sortField -> {
                    String fieldName = STATISTICS_FIELD_MAP.get(sortField.getField());
                    return SortDirection.DESC.equals(sortField.getDirection()) ? Sort.Order.desc(fieldName) :
                            Sort.Order.asc(fieldName);
                })
                .toArray(Sort.Order[]::new);
        return Sort.by(orders);
    }

    private List<EvaluationResultsStatisticsSortField> getSortFields(
            ClassifierOptionsRequest classifierOptionsRequest) {
        if (CollectionUtils.isEmpty(classifierOptionsRequest.getEvaluationResultsStatisticsSortFields())) {
            return ersConfig.getEvaluationResultsSortFields();
        } else {
            return classifierOptionsRequest.getEvaluationResultsStatisticsSortFields();
        }
    }
}
