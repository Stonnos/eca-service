package com.ecaservice.server.model.evaluation;

import eca.core.evaluation.EvaluationMethod;
import lombok.Data;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

/**
 * Evaluation request data.
 *
 * @author Roman Batygin
 */
@Data
public class EvaluationRequestDataModel {

    /**
     * Classifier model
     */
    private AbstractClassifier classifier;

    /**
     * Training data
     */
    private Instances data;

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
