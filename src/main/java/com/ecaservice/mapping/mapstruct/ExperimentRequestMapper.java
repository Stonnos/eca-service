package com.ecaservice.mapping.mapstruct;

import com.ecaservice.dto.ExperimentRequestDto;
import com.ecaservice.model.experiment.ExperimentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @author Roman Batygin
 */
@Mapper
public abstract class ExperimentRequestMapper {

    @Mappings({
            @Mapping(source = "firstName", target = "firstName"),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "experimentType", target = "experimentType"),
            @Mapping(source = "data", target = "data"),
            @Mapping(source = "evaluationMethod", target = "evaluationMethod"),
    })
    public abstract ExperimentRequest map(ExperimentRequestDto experimentRequestDto);
}
