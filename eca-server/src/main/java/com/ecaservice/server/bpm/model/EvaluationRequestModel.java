package com.ecaservice.server.bpm.model;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import eca.core.evaluation.EvaluationMethod;
import lombok.Getter;
import lombok.Setter;

/**
 * Evaluation request data model for bpmn process.
 *
 * @author Roman Batygin
 */
@Getter
@Setter
public class EvaluationRequestModel extends AbstractEvaluationRequestModel {

    /**
     * Classifier options
     */
    private ClassifierOptions classifierOptions;

    /**
     * Use optimal classifier options?
     */
    private boolean useOptimalClassifierOptions;

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
