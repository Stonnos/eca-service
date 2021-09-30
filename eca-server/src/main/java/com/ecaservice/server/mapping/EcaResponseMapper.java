package com.ecaservice.server.mapping;

import com.ecaservice.base.model.ExperimentResponse;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ValueMapping;

/**
 * Experiment response mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface EcaResponseMapper {

    /**
     * Maps experiment to experiment response object.
     *
     * @param experiment experiment
     * @return experiment response model
     */
    @Mapping(source = "requestStatus", target = "status")
    ExperimentResponse map(Experiment experiment);

    /**
     * Maps request status to technical status.
     *
     * @param requestStatus - request status
     * @return technical status
     */
    @ValueMapping(source = "NEW", target = "IN_PROGRESS")
    @ValueMapping(source = "IN_PROGRESS", target = "IN_PROGRESS")
    @ValueMapping(source = "FINISHED", target = "SUCCESS")
    @ValueMapping(source = "TIMEOUT", target = "TIMEOUT")
    @ValueMapping(source = "ERROR", target = "ERROR")
    TechnicalStatus map(RequestStatus requestStatus);
}
