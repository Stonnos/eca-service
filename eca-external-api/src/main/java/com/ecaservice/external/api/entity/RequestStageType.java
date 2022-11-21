package com.ecaservice.external.api.entity;

/**
 * Request stage type.
 *
 * @author Roman Batygin
 */
public enum RequestStageType {

    /**
     * Request sent
     */
    REQUEST_SENT,

    /**
     * Request created
     */
    REQUEST_CREATED,

    /**
     * Request completed
     */
    COMPLETED,

    /**
     * Error status
     */
    ERROR,

    /**
     * Ready to send
     */
    READY,

    /**
     * Request exceeded
     */
    EXCEEDED
}
