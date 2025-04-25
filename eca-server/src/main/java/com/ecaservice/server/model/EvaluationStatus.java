package com.ecaservice.server.model;

/**
 * Evaluation status.
 *
 * @author Roman Batygin
 */
public enum EvaluationStatus {

    /**
     * Success status
     */
    SUCCESS,

    /**
     * Failed status
     */
    FAILED,

    /**
     * Timeout status
     */
    TIMEOUT,

    /**
     * Canceled status
     */
    CANCELED,

    /**
     * Error status
     */
    ERROR
}
