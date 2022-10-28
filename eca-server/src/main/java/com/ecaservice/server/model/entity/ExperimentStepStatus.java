package com.ecaservice.server.model.entity;

/**
 * Experiment step status.
 *
 * @author Roman batygin
 */
public enum ExperimentStepStatus {

    /**
     * Ready
     */
    READY,

    /**
     * Completed
     */
    COMPLETED,

    /**
     * Failed
     */
    FAILED,

    /**
     * Canceled
     */
    CANCELED,

    /**
     * Timeout
     */
    TIMEOUT,

    /**
     * Error
     */
    ERROR
}
