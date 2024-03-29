package com.ecaservice.external.api.test.bpm.model;

/**
 * Task type.
 *
 * @author Roman Batygin
 */
public enum TaskType {

    /**
     * Upload train data file to server
     */
    UPLOAD_TRAINING_DATA,

    /**
     * Send evaluation request
     */
    EVALUATION_REQUEST,

    /**
     * Process evaluation request response
     */
    PROCESS_EVALUATION_REQUEST_RESPONSE,

    /**
     * Get evaluation response status
     */
    GET_EVALUATION_STATUS,

    /**
     * Process instances response
     */
    PROCESS_INSTANCES_RESPONSE,

    /**
     * Compare validation error result
     */
    COMPARE_VALIDATION_ERROR_RESULT,

    /**
     * Compare evaluation response result
     */
    COMPARE_EVALUATION_RESPONSE_RESULT,

    /**
     * Compare downloaded classifier model result
     */
    COMPARE_CLASSIFIER_MODEL_RESULT,

    /**
     * Process final test result
     */
    PROCESS_FINAL_TEST_RESULTS,

    /**
     * Send experiment request
     */
    EXPERIMENT_REQUEST,

    /**
     * Get experiment response status
     */
    GET_EXPERIMENT_STATUS,

    /**
     * Compare downloaded experiment model result
     */
    COMPARE_EXPERIMENT_MODEL_RESULT,

    /**
     * Finish auto test with error
     */
    FINISH_WITH_ERROR
}
