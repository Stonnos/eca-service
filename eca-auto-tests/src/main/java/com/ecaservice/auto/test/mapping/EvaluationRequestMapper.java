package com.ecaservice.auto.test.mapping;

import com.ecaservice.auto.test.dto.EvaluationRequestDto;
import com.ecaservice.auto.test.entity.autotest.EvaluationRequestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Evaluation request mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class EvaluationRequestMapper
        extends BaseEvaluationRequestMapper<EvaluationRequestEntity, EvaluationRequestDto> {

    public EvaluationRequestMapper() {
        super(EvaluationRequestEntity.class);
    }

    /**
     * Maps evaluation request entity to dto model.
     *
     * @param evaluationRequestEntity - evaluation request entity
     * @return evaluation request dto
     */
    @Mapping(source = "evaluationMethod.description", target = "evaluationMethodDescription")
    public abstract EvaluationRequestDto map(EvaluationRequestEntity evaluationRequestEntity);
}
