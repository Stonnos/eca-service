package com.ecaservice.external.api.mapping;

import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.dto.ExApiExperimentType;
import com.ecaservice.external.api.dto.ExperimentRequestDto;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.ExperimentRequestEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static com.ecaservice.external.api.util.Utils.toJson;

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

    /**
     * Maps experiment request dto to entity model.
     *
     * @param experimentRequestDto - experiment request dto
     * @return experiment request entity
     */
    ExperimentRequestEntity map(ExperimentRequestDto experimentRequestDto);

    /**
     * Maps experiment external api experiment type to internal format.
     *
     * @param exApiExperimentType - external api experiment type
     * @return internal experiment type
     */
    ExperimentType map(ExApiExperimentType exApiExperimentType);

    /**
     * Maps classifier options.
     *
     * @param evaluationRequestDto    - evaluation request dto
     * @param evaluationRequestEntity - evaluation request entity
     */
    @AfterMapping
    default void mapClassifierOptions(EvaluationRequestDto evaluationRequestDto,
                                      @MappingTarget EvaluationRequestEntity evaluationRequestEntity) {
        evaluationRequestEntity.setClassifierOptionsJson(toJson(evaluationRequestDto.getClassifierOptions()));
    }
}
