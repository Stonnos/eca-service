package com.ecaservice.mapping;

import com.ecaservice.dto.evaluation.EvaluationMethod;
import org.mapstruct.Mapper;

/**
 * Implements mapping ERS evaluation method enum to model evaluation method.
 *
 * @author Roman Batygin
 */
@Mapper
public interface ErsEvaluationMethodMapper {

    /**
     * Maps ERS evaluation method enum to model evaluation method.
     *
     * @param evaluationMethod - ERS evaluation method
     * @return model evaluation method
     */
    com.ecaservice.model.evaluation.EvaluationMethod map(EvaluationMethod evaluationMethod);
}
