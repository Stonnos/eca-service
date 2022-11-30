package com.ecaservice.external.api.test.mapping;

import com.ecaservice.external.api.test.dto.EvaluationRequestAutoTestDto;
import com.ecaservice.external.api.test.entity.EvaluationRequestAutoTestEntity;
import org.mapstruct.Mapper;

/**
 * Evaluation request auto test mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class EvaluationRequestAutoTestMapper
        extends BaseAutoTestRequestMapper<EvaluationRequestAutoTestEntity, EvaluationRequestAutoTestDto> {

    /**
     * Constructor with parameters.
     */
    protected EvaluationRequestAutoTestMapper() {
        super(EvaluationRequestAutoTestEntity.class);
    }
}
