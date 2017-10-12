package com.ecaservice.mapping;

import com.ecaservice.dto.ExperimentRequestDto;
import com.ecaservice.model.experiment.ExperimentRequest;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

/**
 * @author Roman Batygin
 */
@Component
public class ExperimentRequestConverter extends CustomConverter<ExperimentRequestDto, ExperimentRequest> {

    @Override
    public ExperimentRequest convert(ExperimentRequestDto experimentRequest, Type<? extends ExperimentRequest> type) {
        ExperimentRequest request = new ExperimentRequest();
        request.setData(experimentRequest.getData());
        request.setExperimentType(experimentRequest.getExperimentType());
        request.setEvaluationMethod(experimentRequest.getEvaluationMethod());
        request.setEmail(experimentRequest.getEmail());
        request.setFirstName(experimentRequest.getFirstName());
        return request;
    }
}
