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
     * Training data
     */
    private Instances data;
}
