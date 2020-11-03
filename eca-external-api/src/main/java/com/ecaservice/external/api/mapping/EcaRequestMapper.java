package com.ecaservice.external.api.mapping;

import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import org.mapstruct.Mapper;

/**
 * Eca request mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface EcaRequestMapper {

    /**
     * Maps evaluation request dto to entity model.
     *
     * @param evaluationRequestDto - evaluation request dto
     * @return evaluation request entity
     */
    EvaluationRequestEntity map(EvaluationRequestDto evaluationRequestDto);
}
