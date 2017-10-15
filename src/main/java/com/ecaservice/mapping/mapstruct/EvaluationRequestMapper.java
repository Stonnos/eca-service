package com.ecaservice.mapping.mapstruct;

import com.ecaservice.dto.EvaluationRequestDto;
import com.ecaservice.model.InputData;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.evaluation.EvaluationRequest;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import java.util.Collections;

/**
 * @author Roman Batygin
 */
//@Mapper
public abstract class EvaluationRequestMapper {

    @Mappings({
            @Mapping(source = "data", target = "data"),
            @Mapping(source = "evaluationMethod", target = "evaluationMethod"),
            @Mapping(target = "inputData", ignore = true),
            @Mapping(target = "evaluationOptionsMap", ignore = true)
    })
    public abstract EvaluationRequest map(EvaluationRequestDto evaluationRequestDto);

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
