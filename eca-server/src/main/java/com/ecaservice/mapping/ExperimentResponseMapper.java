package com.ecaservice.mapping;

import com.ecaservice.base.model.ExperimentResponse;
import com.ecaservice.base.model.RequestStage;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.RequestStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ValueMapping;

/**
 * Experiment response mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface ExperimentResponseMapper {

    /**
     * Maps experiment to experiment response object.
     *
     * @param experiment experiment
     * @return experiment response model
     */
    @Mapping(source = "requestStatus", target = "status")
    @Mapping(source = "requestStatus", target = "requestStage")
    ExperimentResponse map(Experiment experiment);

    /**
     * Maps request status to technical status.
     *
     * @param requestStatus - request status
     * @return technical status
     */
    @ValueMapping(source = "NEW", target = "SUCCESS")
    @ValueMapping(source = "IN_PROGRESS", target = "SUCCESS")
    @ValueMapping(source = "FINISHED", target = "SUCCESS")
    @ValueMapping(source = "TIMEOUT", target = "TIMEOUT")
    @ValueMapping(source = "ERROR", target = "ERROR")
    TechnicalStatus map(RequestStatus requestStatus);

    /**
     * Maps request status to stage.
     *
     * @param requestStatus - request status
     * @return request stage
     */
    @ValueMapping(source = "NEW", target = "CREATED")
    @ValueMapping(source = "IN_PROGRESS", target = "CREATED")
    @ValueMapping(source = "FINISHED", target = "FINISHED")
    @ValueMapping(source = "TIMEOUT", target = "FINISHED")
    @ValueMapping(source = "ERROR", target = "FINISHED")
    RequestStage mapRequestStage(RequestStatus requestStatus);
}
