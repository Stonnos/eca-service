package com.ecaservice.mapping;

import com.ecaservice.model.evaluation.EvaluationMethod;
import org.mapstruct.Mapper;

/**
 * Evaluation method mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface EvaluationMethodMapper {

    /**
     * Maps evaluation method enum to eca-core evaluation method enum.
     *
     * @param evaluationMethod evaluation method
     * @return mapped {@link eca.core.evaluation.EvaluationMethod}
     */
    eca.core.evaluation.EvaluationMethod map(EvaluationMethod evaluationMethod);
}
