package com.ecaservice.mapping;

import com.ecaservice.dto.ExperimentRequestDto;
import com.ecaservice.model.experiment.ExperimentRequest;
import org.mapstruct.Mapper;

/**
 * Experiment request mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface ExperimentRequestMapper {

    /**
     * Maps experiment request dto to experiment request.
     *
     * @param experimentRequestDto {@link ExperimentRequestDto} object
     * @return {@link ExperimentRequest} object
     */
    ExperimentRequest map(ExperimentRequestDto experimentRequestDto);
}
