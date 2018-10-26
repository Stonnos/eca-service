package com.ecaservice.model.evaluation;

/**
 * Interface for visitor pattern.
 *
 * @author Roman Batygin
 */
public interface EvaluationMethodVisitor {

    /**
     * Method executed in case if evaluation method is TRAINING_DATA
     *
     * @throws Exception
     */
    void evaluateModel() throws Exception;

    /**
     * Method executed in case if evaluation method is CROSS_VALIDATION
     *
     * @throws Exception
     */
    void crossValidateModel() throws Exception;
}
