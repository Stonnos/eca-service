package com.ecaservice.core.audit.entity;

/**
 * Event status enum.
 *
 * @author Roman Batygin
 */
public enum EventStatus {

    /**
     * Audit event successfully sent.
     */
    SENT,

    /**
     * Audit event not sent, because of service unavailable.
     */
    NOT_SENT,

    /**
     * Audit event request with error
     */
    ERROR,
}
