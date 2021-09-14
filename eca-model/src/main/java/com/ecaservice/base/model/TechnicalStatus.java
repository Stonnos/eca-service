package com.ecaservice.base.model;

/**
 * Evaluation response technical status.
 *
 * @author Roman Batygin
 */
public enum TechnicalStatus {

    /**
     * Evaluation in progress.
     */
    IN_PROGRESS,

    /**
     * Evaluation success status.
     */
    SUCCESS,

    /**
     * Error status.
     */
    ERROR,

    /**
     * Timeout status.
     */
    TIMEOUT,

    /**
     * Validation error status.
     */
    VALIDATION_ERROR
}
