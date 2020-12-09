package com.ecaservice.notification.dto;

/**
 * Email type.
 *
 * @author Roman Batygin
 */
public enum EmailType {

    /**
     * New experiment
     */
    NEW_EXPERIMENT,

    /**
     * Finished experiment
     */
    FINISHED_EXPERIMENT,

    /**
     * Error experiment
     */
    ERROR_EXPERIMENT,

    /**
     * Timeout experiment
     */
    TIMEOUT_EXPERIMENT,

    /**
     * New user
     */
    NEW_USER,

    /**
     * Reset password
     */
    RESET_PASSWORD,

    /**
     * Two factor authentication code
     */
    TFA_CODE
}
