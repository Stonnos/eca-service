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
     * Response received
     */
    RESPONSE_RECEIVED,

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
