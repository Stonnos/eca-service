package com.ecaservice.external.api.test.mapping;

import com.ecaservice.external.api.test.dto.ExperimentRequestAutoTestDto;
import com.ecaservice.external.api.test.entity.ExperimentRequestAutoTestEntity;
import org.mapstruct.Mapper;

/**
 * Experiment request auto test mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class ExperimentRequestAutoTestMapper
        extends BaseAutoTestRequestMapper<ExperimentRequestAutoTestEntity, ExperimentRequestAutoTestDto> {

    /**
     * Constructor with parameters.
     */
    protected ExperimentRequestAutoTestMapper() {
        super(ExperimentRequestAutoTestEntity.class);
    }
}
