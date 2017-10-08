package com.ecaservice.model;

/**
 * Evaluation status enum.
 *
 * @author Roman Batygin
 */
public enum EvaluationStatus {

    /**
     * New status
     */
    NEW,

    /**
     * Finished status
     */
    FINISHED,

    /**
     * Timeout status
     */
    TIMEOUT,

    /**
     * Error status
     */
    ERROR,

    /**
     * Email not send status
     */
    FAILED,

    /**
     * Exceed status
     */
    EXCEEDED
}
