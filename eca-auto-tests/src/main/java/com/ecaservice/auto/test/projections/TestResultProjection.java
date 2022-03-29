package com.ecaservice.auto.test.projections;

import com.ecaservice.test.common.model.TestResult;

/**
 * Test result projection interface.
 *
 * @author Roman Batygin
 */
public interface TestResultProjection {

    /**
     * Gets test result.
     *
     * @return test result
     */
    TestResult getTestResult();

    /**
     * Gets total matched.
     *
     * @return total matched
     */
    int getTotalMatched();

    /**
     * Gets total not matched.
     *
     * @return total not matched
     */
    int getTotalNotMatched();

    /**
     * Gets total not found.
     *
     * @return total not found
     */
    int getTotalNotFound();
}
