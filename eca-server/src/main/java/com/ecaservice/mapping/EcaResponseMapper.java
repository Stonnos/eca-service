package com.ecaservice.mapping;

import com.ecaservice.dto.EcaResponse;
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
    @Mapping(source = "uuid", target = "requestId")
    @Mapping(target = "status", constant = "SUCCESS")
    @Mapping(target = "errorMessage", ignore = true)
    EcaResponse map(Experiment experiment);
}
