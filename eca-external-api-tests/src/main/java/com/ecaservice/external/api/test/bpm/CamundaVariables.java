package com.ecaservice.external.api.test.bpm;

import lombok.experimental.UtilityClass;

/**
 * Camunda variables names constants.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class CamundaVariables {

    /**
     * Execution result variable
     */
    public static final String EXECUTION_RESULT = "executionResult";

    /**
     * Task type variable
     */
    public static final String TASK_TYPE = "taskType";

    /**
     * Auto test id variable
     */
    public static final String AUTO_TEST_ID = "autoTestId";

    /**
     * Test type variable
     */
    public static final String TEST_TYPE = "testType";

    /**
     * Test data model variable
     */
    public static final String TEST_DATA_MODEL = "testDataModel";

    /**
     * Test results matcher variable
     */
    public static final String TEST_RESULTS_MATCHER = "testResultsMatcher";

    /**
     * API response variable
     */
    public static final String API_RESPONSE = "apiResponse";
}
