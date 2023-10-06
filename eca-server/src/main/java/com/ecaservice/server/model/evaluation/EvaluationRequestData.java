package com.ecaservice.server.model.evaluation;

import com.ecaservice.server.model.AbstractEvaluationRequestData;
import eca.core.evaluation.EvaluationMethod;
import lombok.Getter;
import lombok.Setter;
import weka.classifiers.AbstractClassifier;

/**
 * Evaluation request data model.
 *
 * @author Roman Batygin
 */
@Getter
@Setter
public class EvaluationRequestData extends AbstractEvaluationRequestData {

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
