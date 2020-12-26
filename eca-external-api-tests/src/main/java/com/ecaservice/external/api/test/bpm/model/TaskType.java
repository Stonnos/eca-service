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
    COMPARE_EVALUATION_RESPONSE_RESULT
}
