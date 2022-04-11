package com.ecaservice.auto.test.model;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import eca.core.evaluation.EvaluationMethod;
import lombok.Data;

/**
 * Classifier test data model.
 *
 * @author Roman Batygin
 */
@Data
public class ClassifierTestDataModel {

    /**
     * Train data path in resources directory
     */
    private String trainDataPath;

    /**
     * Classifier options
     */
    private ClassifierOptions classifierOptions;

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
     * Seed value for random generator
     */
    private Integer seed;
}
