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

    @Mappings({
            @Mapping(source = "firstName", target = "firstName"),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "experimentType", target = "experimentType"),
            @Mapping(source = "evaluationMethod", target = "evaluationMethod"),
            @Mapping(source = "ipAddress", target = "ipAddress"),
    })
    public abstract Experiment map(ExperimentRequest experimentRequest);
}
