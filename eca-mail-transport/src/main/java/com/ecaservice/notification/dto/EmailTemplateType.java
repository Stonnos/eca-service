package com.ecaservice.notification.dto;

/**
 * Email template type.
 *
 * @author Roman Batygin
 */
public enum EmailTemplateType {

    /**
     * New experiment template
     */
    NEW_EXPERIMENT_TEMPLATE,

    /**
     * Finished experiment template
     */
    FINISHED_EXPERIMENT_TEMPLATE,

    /**
     * Error experiment template
     */
    ERROR_EXPERIMENT_TEMPLATE,

    /**
     * Timeout experiment template
     */
    TIMEOUT_EXPERIMENT_TEMPLATE,

    /**
     * New user template
     */
    NEW_USER_TEMPLATE
}
