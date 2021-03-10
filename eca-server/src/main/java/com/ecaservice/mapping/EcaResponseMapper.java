package com.ecaservice.mapping;

import com.ecaservice.base.model.EcaResponse;
import com.ecaservice.model.entity.Experiment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Eca response mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface EcaResponseMapper {

    /**
     * Maps experiment to eca response object.
     *
     * @param experiment experiment
     * @return eca response model
     */
    @Mapping(target = "status", constant = "SUCCESS")
    EcaResponse map(Experiment experiment);
}
