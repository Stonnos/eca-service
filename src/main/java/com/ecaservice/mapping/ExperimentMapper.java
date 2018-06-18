package com.ecaservice.mapping;

import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.model.entity.Experiment;
import org.mapstruct.Mapper;

/**
 * Experiment mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface ExperimentMapper {

    /**
     * Maps experiment request to experiment persistence entity.
     *
     * @param experimentRequest experiment request
     * @return experiment entity
     */
    Experiment map(ExperimentRequest experimentRequest);
}
