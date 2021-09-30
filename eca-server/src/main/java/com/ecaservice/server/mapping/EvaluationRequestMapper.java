package com.ecaservice.server.mapping;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.EvaluationMethodReport;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigInteger;
import java.util.Optional;

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
                com.ecaservice.ers.dto.EvaluationMethod.CROSS_VALIDATION.equals(
                        classifierOptionsRequest.getEvaluationMethodReport().getEvaluationMethod())) {
            EvaluationMethodReport evaluationMethodReport = classifierOptionsRequest.getEvaluationMethodReport();
            evaluationRequest.setNumFolds(
                    Optional.ofNullable(evaluationMethodReport.getNumFolds()).map(BigInteger::intValue).orElse(null));
            evaluationRequest.setNumTests(
                    Optional.ofNullable(evaluationMethodReport.getNumTests()).map(BigInteger::intValue).orElse(null));
            evaluationRequest.setSeed(
                    Optional.ofNullable(evaluationMethodReport.getSeed()).map(BigInteger::intValue).orElse(null));
        }
    }
}
