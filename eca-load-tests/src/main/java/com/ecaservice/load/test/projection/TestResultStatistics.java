package com.ecaservice.load.test.projection;

import com.ecaservice.test.common.model.TestResult;

/**
 * Test result statistics projection.
 *
 * @author Roman Batygin
 */
public interface TestResultStatistics {

    /**
     * Gets test result.
     *
     * @return test result
     */
    TestResult getTestResult();

    /**
     * Gets test result count.
     *
     * @return test result count
     */
    int getTestResultCount();
}
