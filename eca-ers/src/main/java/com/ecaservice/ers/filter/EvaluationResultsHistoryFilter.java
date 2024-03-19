package com.ecaservice.ers.filter;

import com.ecaservice.core.filter.specification.AbstractFilter;
import com.ecaservice.ers.model.EvaluationResultsInfo;
import com.ecaservice.web.dto.model.FilterRequestDto;

import java.util.List;

/**
 * Implements evaluation results history filter.
 *
 * @author Roman Batygin
 */
public class EvaluationResultsHistoryFilter extends AbstractFilter<EvaluationResultsInfo> {

    /**
     * Constructor with filters requests.
     *
     * @param globalFilterFields - global filter fields list
     * @param searchQuery        - search query string
     * @param filters            - filters requests list
     */
    public EvaluationResultsHistoryFilter(String searchQuery, List<String> globalFilterFields, List<FilterRequestDto> filters) {
        super(EvaluationResultsInfo.class, searchQuery, globalFilterFields, filters);
    }
}
