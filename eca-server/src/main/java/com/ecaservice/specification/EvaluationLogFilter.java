package com.ecaservice.specification;

import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.web.dto.FilterRequestDto;

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
     * @param filters - filters requests list
     */
    public EvaluationLogFilter(List<FilterRequestDto> filters) {
        super(EvaluationLog.class, filters);
    }
}
