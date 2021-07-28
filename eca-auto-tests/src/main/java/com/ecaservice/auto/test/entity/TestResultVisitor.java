package com.ecaservice.auto.test.entity;

/**
 * Test result visitor.
 *
 * @author Roman Batygin
 */
public interface TestResultVisitor {

    /**
     * Visit in case of PASSES.
     */
    void casePassed();

    /**
     * Visit in case of FAILED.
     */
    void caseFailed();

    /**
     * Visit in case of ERROR.
     */
    void caseError();

    /**
     * Visit in case of UNKNOWN.
     */
    default void caseUnknown() {
    }
}
