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

    public abstract ExperimentRequest map(ExperimentRequestDto experimentRequestDto);
}
