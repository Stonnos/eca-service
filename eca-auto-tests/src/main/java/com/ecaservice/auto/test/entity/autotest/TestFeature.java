package com.ecaservice.auto.test.entity.autotest;

/**
 * Test feature enum.
 *
 * @author Roman Batygin
 */
public enum TestFeature {

    /**
     * Checks evaluation results
     */
    EVALUATION_RESULTS {
        @Override
        public <T> T visit(TestFeatureVisitor<T> visitor) {
            return visitor.visitEvaluationResults();
        }
    },

    /**
     * Checks experiment email notifications
     */
    EXPERIMENT_EMAILS {
        @Override
        public <T> T visit(TestFeatureVisitor<T> visitor) {
            return visitor.visitExperimentEmailsFeature();
        }
    };

    /**
     * Calls visitor.
     *
     * @param visitor - visitor interface
     * @param <T>     - result generic type
     * @return result
     */
    public abstract <T> T visit(TestFeatureVisitor<T> visitor);
}
