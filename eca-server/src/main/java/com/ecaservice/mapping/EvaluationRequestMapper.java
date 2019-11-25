package com.ecaservice.mapping;

import com.ecaservice.dto.EvaluationRequest;
import com.ecaservice.dto.evaluation.ClassifierOptionsRequest;
import com.ecaservice.model.evaluation.EvaluationOption;
import eca.util.Utils;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.EnumMap;

/**
 * Evaluation request mapper.
 *
 * @author Roman Batygin
 */
@Mapper(uses = ErsEvaluationMethodMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class EvaluationRequestMapper {

    /**
     * Maps to evaluation request.
     *
     * @param classifierOptionsRequest - classifier options request
     * @return evaluation request
     */
    @Mapping(source = "classifierOptionsRequest.evaluationMethodReport.evaluationMethod", target = "evaluationMethod")
    public abstract EvaluationRequest map(ClassifierOptionsRequest classifierOptionsRequest);

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
                    classifierOptionsRequest.getEvaluationMethodReport().getSeed());
        }
    }
}
