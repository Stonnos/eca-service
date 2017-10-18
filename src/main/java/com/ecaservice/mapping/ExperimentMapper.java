package com.ecaservice.mapping;

import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentRequest;
import org.mapstruct.Mapper;

/**
 * @author Roman Batygin
 */
@Mapper
public abstract class ExperimentMapper {

    public abstract Experiment map(ExperimentRequest experimentRequest);
}
