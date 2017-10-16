package com.ecaservice.mapping.mapstruct;

import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @author Roman Batygin
 */
@Mapper
public abstract class ExperimentMapper {

    public abstract Experiment map(ExperimentRequest experimentRequest);
}
