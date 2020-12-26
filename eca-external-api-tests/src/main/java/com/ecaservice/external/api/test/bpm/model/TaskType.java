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
     * Compare validation error result
     */
    COMPARE_VALIDATION_ERROR_RESULT
}
