package com.ecaservice.server.model.evaluation;

import lombok.AllArgsConstructor;
import lombok.Data;
import weka.core.Instances;

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
     * Training data md5 hash
     */
    private String dataMd5Hash;

    /**
     * Training data
     */
    private Instances data;
}
