package com.ecaservice.mapping;

import com.ecaservice.dto.EvaluationRequestDto;
import com.ecaservice.dto.evaluation.ClassifierOptionsRequest;
import com.ecaservice.model.InputData;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.evaluation.EvaluationOption;
import com.ecaservice.model.evaluation.EvaluationRequest;
import eca.util.Utils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import java.util.Collections;
import java.util.EnumMap;

/**
 * Evaluation request mapper.
 *
 * @author Roman Batygin
 */
@Mapper(uses = ErsEvaluationMethodMapper.class)
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


    /**
     * Maps to evaluation request.
     *
     * @param classifierOptionsRequest - classifier options request
     * @param inputData                - input data
     * @return evaluation request
     */
    @Mappings( {
            @Mapping(source = "classifierOptionsRequest.evaluationMethodReport.evaluationMethod",
                    target = "evaluationMethod")
    })
    public abstract EvaluationRequest map(ClassifierOptionsRequest classifierOptionsRequest, InputData inputData);

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

    @AfterMapping
    protected void afterMapping(ClassifierOptionsRequest classifierOptionsRequest,
                                @MappingTarget EvaluationRequest evaluationRequest) {
        if (classifierOptionsRequest.getEvaluationMethodReport() != null &&
                com.ecaservice.dto.evaluation.EvaluationMethod.CROSS_VALIDATION.equals(
                        classifierOptionsRequest.getEvaluationMethodReport().getEvaluationMethod())) {
            evaluationRequest.setEvaluationOptionsMap(new EnumMap<>(EvaluationOption.class));
            Utils.putValueIfNotNull(evaluationRequest.getEvaluationOptionsMap(), EvaluationOption.NUM_FOLDS,
                    classifierOptionsRequest.getEvaluationMethodReport().getNumFolds());
            Utils.putValueIfNotNull(evaluationRequest.getEvaluationOptionsMap(), EvaluationOption.NUM_TESTS,
                    classifierOptionsRequest.getEvaluationMethodReport().getNumTests());
            Utils.putValueIfNotNull(evaluationRequest.getEvaluationOptionsMap(), EvaluationOption.SEED,
                    classifierOptionsRequest.getEvaluationMethodReport().getNumTests());
        }
    }
}
