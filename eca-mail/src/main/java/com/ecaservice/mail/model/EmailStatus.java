package com.ecaservice.mail.model;

/**
 * Email status
 *
 * @author Roman Batygin
 */
public enum EmailStatus {

    /**
     * New status
     */
    NEW,

    /**
     * Email has been sent
     */
    SENT,

    /**
     * Email not sent status
     */
    NOT_SENT,

    /**
     * Exceeded status
     */
    EXCEEDED
}
