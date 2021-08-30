package com.ecaservice.external.api.mapping;

import com.ecaservice.external.api.dto.EvaluationStatus;
import com.ecaservice.external.api.entity.RequestStageType;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;

/**
 * Evaluation status mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface EvaluationStatusMapper {

    /**
     * Maps request stage type to evaluation status.
     *
     * @param requestStageType - request stage type
     * @return evaluation status
     */
    @ValueMapping(source = "REQUEST_SENT", target = "IN_PROGRESS")
    @ValueMapping(source = "RESPONSE_RECEIVED", target = "IN_PROGRESS")
    @ValueMapping(source = "COMPLETED", target = "FINISHED")
    @ValueMapping(source = "ERROR", target = "ERROR")
    @ValueMapping(source = "NOT_SEND", target = "IN_PROGRESS")
    @ValueMapping(source = "EXCEEDED", target = "TIMEOUT")
    EvaluationStatus map(RequestStageType requestStageType);
}
