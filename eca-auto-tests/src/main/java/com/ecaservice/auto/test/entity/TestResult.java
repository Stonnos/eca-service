package com.ecaservice.auto.test.entity;

/**
 * Test result enum.
 *
 * @author Roman Batygin
 */
public enum TestResult {

    /**
     * Test passed
     */
    PASSED {
        @Override
        public void apply(TestResultVisitor visitor) {
            visitor.casePassed();
        }
    },

    /**
     * Test failed
     */
    FAILED {
        @Override
        public void apply(TestResultVisitor visitor) {
            visitor.caseFailed();
        }
    },

    /**
     * Unknown error
     */
    ERROR {
        @Override
        public void apply(TestResultVisitor visitor) {
            visitor.caseError();
        }
    },

    /**
     * Unknown result
     */
    UNKNOWN {
        @Override
        public void apply(TestResultVisitor visitor) {
            visitor.caseUnknown();
        }
    };

    /**
     * Apply visitor.
     *
     * @param visitor - visitor object
     */
    public abstract void apply(TestResultVisitor visitor);
}
