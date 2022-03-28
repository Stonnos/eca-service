package com.ecaservice.auto.test.entity.autotest;

/**
 * Test feature enum.
 *
 * @author Roman Batygin
 */
public enum TestFeature {

    /**
     * Checks experiment email notifications
     */
    EXPERIMENT_EMAILS {
        @Override
        public void visit(TestFeatureVisitor visitor) {
            visitor.visitExperimentEmailsFeature();
        }
    };

    /**
     * Calls visitor.
     *
     * @param visitor - visitor interface
     */
    public abstract void visit(TestFeatureVisitor visitor);
}
