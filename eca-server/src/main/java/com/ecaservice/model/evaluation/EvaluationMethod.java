package com.ecaservice.model.evaluation;

/**
 * Evaluation method enum.
 *
 * @author Roman Batygin
 */
public enum EvaluationMethod {

    /**
     * Training data method
     */
    TRAINING_DATA {
        @Override
        public void handle(EvaluationMethodVisitor visitor) throws Exception {
            visitor.evaluateModel();
        }
    },

    /**
     * k * V cross - validation method
     */
    CROSS_VALIDATION {
        @Override
        public void handle(EvaluationMethodVisitor visitor) throws Exception {
            visitor.crossValidateModel();
        }
    };

    /**
     * Visitor pattern common method
     *
     * @param visitor visitor class
     * @throws Exception
     */
    public abstract void handle(EvaluationMethodVisitor visitor) throws Exception;
}
