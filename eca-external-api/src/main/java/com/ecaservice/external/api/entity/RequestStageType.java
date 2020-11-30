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
     * Request not send
     */
    NOT_SEND,

    /**
     * Request exceeded
     */
    EXCEEDED
}
