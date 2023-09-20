package com.ecaservice.server.model.evaluation;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Instances request data model.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
public class InstancesRequestDataModel {

    /**
     * Request id
     */
    private String requestId;

    /**
     * Train data uuid
     */
    private String dataUuid;
}
