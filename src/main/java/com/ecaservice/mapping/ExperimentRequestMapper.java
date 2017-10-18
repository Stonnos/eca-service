package com.ecaservice.mapping;

import com.ecaservice.dto.ExperimentRequestDto;
import com.ecaservice.model.experiment.ExperimentRequest;
import org.mapstruct.Mapper;

/**
 * @author Roman Batygin
 */
@Mapper
public abstract class ExperimentRequestMapper {

    public abstract ExperimentRequest map(ExperimentRequestDto experimentRequestDto);
}
