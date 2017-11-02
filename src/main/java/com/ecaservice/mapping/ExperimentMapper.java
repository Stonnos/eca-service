package com.ecaservice.mapping;

import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentRequest;
import org.mapstruct.Mapper;

/**
 * Experiment mapper.
 * @author Roman Batygin
 */
@Mapper
public interface ExperimentMapper {

    /**
     * Maps experiment request to experiment persistence entity.
     *
     * @param experimentRequest {@link ExperimentRequest} object
     * @return {@link Experiment} object
     */
    Experiment map(ExperimentRequest experimentRequest);
}
