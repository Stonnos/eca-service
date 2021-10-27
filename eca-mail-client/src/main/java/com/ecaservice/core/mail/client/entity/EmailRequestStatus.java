package com.ecaservice.core.mail.client.entity;

/**
 * Email request status enum.
 *
 * @author Roman Batygin
 */
public enum EmailRequestStatus {

    /**
     * Email request successfully sent.
     */
    SENT,

    /**
     * Email request not sent, because of service unavailable.
     */
    NOT_SENT,

    /**
     * Email request with error
     */
    ERROR,

    /**
     * Email request exceeded, because of cache duration expired
     */
    EXCEEDED
}
