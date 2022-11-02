package com.ecaservice.external.api.mapping;

import com.ecaservice.base.model.ErrorCode;
import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.external.api.dto.EvaluationErrorCode;
import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.dto.ExApiExperimentType;
import com.ecaservice.external.api.dto.ExperimentRequestDto;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.ExperimentRequestEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ValueMapping;

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
     * Maps eca - server error code to evaluation error code
     *
     * @param errorCode - error code from eca - server
     * @return evaluation error code
     */
    @ValueMapping(source = "INTERNAL_SERVER_ERROR", target = "INTERNAL_SERVER_ERROR")
    @ValueMapping(source = "SERVICE_UNAVAILABLE", target = "SERVICE_UNAVAILABLE")
    @ValueMapping(source = "TRAINING_DATA_NOT_FOUND", target = "TRAINING_DATA_NOT_FOUND")
    @ValueMapping(source = "INVALID_FIELD_VALUE", target = "INTERNAL_SERVER_ERROR")
    @ValueMapping(source = "CLASSIFIER_OPTIONS_NOT_FOUND", target = "CLASSIFIER_OPTIONS_NOT_FOUND")
    EvaluationErrorCode map(ErrorCode errorCode);

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
