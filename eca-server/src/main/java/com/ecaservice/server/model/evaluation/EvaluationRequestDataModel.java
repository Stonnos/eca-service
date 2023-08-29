package com.ecaservice.server.model.evaluation;

import eca.core.evaluation.EvaluationMethod;
import lombok.Data;
import weka.classifiers.AbstractClassifier;

/**
 * Evaluation request data model.
 *
 * @author Roman Batygin
 */
@Data
public class EvaluationRequestDataModel {

    /**
     * Request id
     */
    private String requestId;

    /**
     * Train data uuid
     */
    private String dataUuid;

    /**
     * Classifier model
     */
    private AbstractClassifier classifier;

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
