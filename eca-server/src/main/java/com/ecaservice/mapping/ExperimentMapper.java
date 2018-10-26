package com.ecaservice.mapping;

import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.web.dto.ExperimentDto;
import org.mapstruct.Mapper;

import java.util.List;

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

    /**
     * Maps experiment entity to experiment dto model.
     *
     * @param experiment - experiment entity
     * @return experiment dto model
     */
    ExperimentDto map(Experiment experiment);

    /**
     * Maps experiment entities to experiment dto models.
     *
     * @param experiment - experiment entities list
     * @return experiment dto models list
     */
    List<ExperimentDto> map(List<Experiment> experiment);
}
