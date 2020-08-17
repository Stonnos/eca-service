package com.ecaservice.mapping;

import com.ecaservice.model.entity.ExperimentProgressEntity;
import com.ecaservice.web.dto.model.ExperimentProgressDto;
import org.mapstruct.Mapper;

/**
 * Experiment progress mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface ExperimentProgressMapper {

    /**
     * Maps experiment progress entity to dto model.
     *
     * @param experimentProgressEntity - experiment progress entity
     * @return experiment progress dto
     */
    ExperimentProgressDto map(ExperimentProgressEntity experimentProgressEntity);
}
