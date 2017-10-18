package com.ecaservice.mapping;

import com.ecaservice.model.evaluation.EvaluationMethod;
import org.mapstruct.Mapper;

/**
 * @author Roman Batygin
 */
@Mapper
public abstract class EvaluationMethodMapper {

    public abstract eca.core.evaluation.EvaluationMethod map(EvaluationMethod evaluationMethod);
}
