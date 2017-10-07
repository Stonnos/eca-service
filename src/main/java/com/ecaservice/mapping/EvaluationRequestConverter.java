package com.ecaservice.mapping;

import com.ecaservice.dto.EvaluationRequestDto;
import com.ecaservice.model.EvaluationMethod;
import com.ecaservice.model.EvaluationRequest;
import com.ecaservice.model.InputData;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Implements the conversion of {@link EvaluationRequestDto} object into the {@link EvaluationRequest} object.
 *
 * @author Roman Batygin
 */
@Component
public class EvaluationRequestConverter extends CustomConverter<EvaluationRequestDto, EvaluationRequest> {

    @Override
    public EvaluationRequest convert(EvaluationRequestDto evaluationRequestDto, Type<? extends EvaluationRequest> type) {
        EvaluationRequest evaluationRequest = new EvaluationRequest();
        evaluationRequest.setInputData(new InputData(evaluationRequestDto.getClassifier(),
                evaluationRequestDto.getData()));
        evaluationRequest.setEvaluationMethod(evaluationRequestDto.getEvaluationMethod());
        if (EvaluationMethod.CROSS_VALIDATION.equals(evaluationRequest.getEvaluationMethod())) {
            evaluationRequest.setEvaluationOptionsMap(evaluationRequestDto.getEvaluationOptionsMap());
        } else {
            evaluationRequest.setEvaluationOptionsMap(Collections.emptyMap());
        }
        return evaluationRequest;
    }
}
