package com.ecaservice.mapping;

import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentRequest;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

/**
 * @author Roman Batygin
 */
@Component
public class ExperimentRequestConverter extends CustomConverter<ExperimentRequest, Experiment> {

    @Override
    public Experiment convert(ExperimentRequest experimentRequest, Type<? extends Experiment> type) {
        Experiment experiment = new Experiment();
        experiment.setFirstName(experimentRequest.getFirstName());
        experiment.setEmail(experimentRequest.getEmail());
        experiment.setIpAddress(experimentRequest.getIpAddress());
        experiment.setExperimentType(experimentRequest.getExperimentType());
        experiment.setEvaluationMethod(experimentRequest.getEvaluationMethod());
        return experiment;
    }
}
