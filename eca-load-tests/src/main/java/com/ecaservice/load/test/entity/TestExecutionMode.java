package com.ecaservice.load.test.entity;

/**
 * Test execution mode.
 *
 * @author Roman Batygin
 */
public enum TestExecutionMode {

    /**
     * Test execution in specified duration.
     */
    DURATION {
        @Override
        public void visit(TestExecutionModeVisitor visitor) {
            visitor.visitDuration();
        }
    },

    /**
     * Specified requests limit execution.
     */
    REQUESTS_LIMIT {
        @Override
        public void visit(TestExecutionModeVisitor visitor) {
            visitor.visitRequestsLimit();
        }
    };

    /**
     * Invokes visitor.
     *
     * @param visitor        - visitor interface
     */
    public abstract void visit(TestExecutionModeVisitor visitor);
}
