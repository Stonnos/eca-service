package com.ecaservice.model.evaluation;

/**
 * Evaluation option variables enum.
 *
 * @author Roman Batygin
 */
public enum EvaluationOption {

    /**
     * Number of folds in k * V cross validation method
     */
    NUM_FOLDS,

    /**
     * Number of tests in k * V cross validation method
     */
    NUM_TESTS,

    /**
     * Seed value for random generator
     */
    SEED
}
