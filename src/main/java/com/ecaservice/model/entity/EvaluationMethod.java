package com.ecaservice.model.entity;

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
        public void accept(EvaluationMethodVisitor visitor) throws Exception {
            visitor.evaluateModel();
        }
    },

    /**
     * k * V cross - validation method
     */
    CROSS_VALIDATION {
        @Override
        public void accept(EvaluationMethodVisitor visitor) throws Exception {
            visitor.crossValidateModel();
        }
    };

    /**
     * Visitor pattern common method
     *
     * @param visitor visitor class
     * @throws Exception
     */
    public abstract void accept(EvaluationMethodVisitor visitor) throws Exception;
}
