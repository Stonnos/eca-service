package com.ecaservice.server.model.evaluation;

import eca.core.evaluation.EvaluationMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Instances request data model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
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

    /**
     * Evaluation method
     */
    private EvaluationMethod evaluationMethod;

    /**
     * Folds number for k * V cross - validation method
     */
    private Integer numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    private Integer numTests;

    /**
     * Seed value for k * V cross - validation method
     */
    private Integer seed;
}
