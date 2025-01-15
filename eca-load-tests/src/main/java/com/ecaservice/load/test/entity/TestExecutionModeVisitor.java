package com.ecaservice.load.test.entity;

/**
 * Test execution mode visitor.
 *
 * @author Roman Batygin
 */
public interface TestExecutionModeVisitor {

    /**
     * Visits duration mode.
     */
    void visitDuration();

    /**
     * Visits requests limit mode.
     */
    void visitRequestsLimit();
}
