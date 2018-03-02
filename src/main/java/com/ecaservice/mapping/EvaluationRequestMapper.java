package com.ecaservice.mapping;

import com.ecaservice.dto.EvaluationRequestDto;
import com.ecaservice.model.InputData;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.evaluation.EvaluationRequest;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import java.util.Collections;

/**
 * Evaluation request mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class EvaluationRequestMapper {

    /**
     * Maps evaluation request dto to evaluation request.
     *
     * @param evaluationRequestDto evaluation request dto model
     * @return evaluation request
     */
    @Mappings( {
            @Mapping(target = "evaluationOptionsMap", ignore = true)
    })
    public abstract EvaluationRequest map(EvaluationRequestDto evaluationRequestDto);

    @AfterMapping
    protected void afterMapping(EvaluationRequestDto evaluationRequestDto,
                                @MappingTarget EvaluationRequest evaluationRequest) {
        evaluationRequest.setInputData(new InputData(evaluationRequestDto.getClassifier(),
                evaluationRequestDto.getData()));
        if (EvaluationMethod.CROSS_VALIDATION.equals(evaluationRequestDto.getEvaluationMethod())) {
            evaluationRequest.setEvaluationOptionsMap(evaluationRequestDto.getEvaluationOptionsMap());
        } else {
            evaluationRequest.setEvaluationOptionsMap(Collections.emptyMap());
        }
    }
}
