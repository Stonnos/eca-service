package com.ecaservice.ers.dto;

/**
 * Evaluation method.
 *
 * @author Roman Batygin
 */
public enum EvaluationMethod {

    /**
     * Use training data
     */
    TRAINING_DATA,

    /**
     * Use k * V - folds cross - validation method
     */
    CROSS_VALIDATION
}
