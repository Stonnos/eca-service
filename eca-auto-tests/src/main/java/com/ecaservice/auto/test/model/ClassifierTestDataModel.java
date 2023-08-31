package com.ecaservice.auto.test.model;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import eca.core.evaluation.EvaluationMethod;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Classifier test data model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ClassifierTestDataModel extends AbstractEvaluationTestDataModel {

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
