package com.ecaservice.filter;

import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.web.dto.model.FilterRequestDto;

import java.util.List;

/**
 * Implements evaluation log filter.
 *
 * @author Roman Batygin
 */
public class EvaluationLogFilter extends AbstractFilter<EvaluationLog> {

    /**
     * Constructor with filters requests.
     *
     * @param searchQuery - search query string
     * @param filters     - filters requests list
     */
    public EvaluationLogFilter(String searchQuery, List<FilterRequestDto> filters) {
        super(EvaluationLog.class, searchQuery, filters);
    }
}
