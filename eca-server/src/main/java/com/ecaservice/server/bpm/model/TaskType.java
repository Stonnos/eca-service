package com.ecaservice.server.bpm.model;

/**
 * Task type.
 *
 * @author Roman Batygin
 */
public enum TaskType {

    /**
     * Gets experiment details
     */
    GET_EXPERIMENT_DETAILS,

    /**
     * Starts experiment
     */
    START_EXPERIMENT,

    /**
     * Finishes experiment
     */
    FINISH_EXPERIMENT,

    /**
     * Finishes experiment with error
     */
    FINISH_EXPERIMENT_WITH_ERROR,

    /**
     * Finishes experiment with timeout
     */
    FINISH_EXPERIMENT_WITH_TIMEOUT,

    /**
     * Process experiment
     */
    PROCESS_EXPERIMENT,

    /**
     * Gets experiment process status
     */
    GET_EXPERIMENT_PROCESS_STATUS,

    /**
     * Sent experiment email notification
     */
    SENT_EXPERIMENT_EMAIL,

    /**
     * Sent experiment system push notification
     */
    SENT_EXPERIMENT_SYSTEM_PUSH,

    /**
     * Sent experiment web push notification
     */
    SENT_EXPERIMENT_WEB_PUSH,

    /**
     * Sent experiment response to MQ
     */
    SENT_EXPERIMENT_RESPONSE,

    /**
     * Gets user info
     */
    GET_USER_INFO,

    /**
     * Export valid instances to central data storage
     */
    EXPORT_VALID_INSTANCES,

    /**
     * Create experiment request
     */
    CREATE_EXPERIMENT_REQUEST,

    /**
     * Create experiment web request
     */
    CREATE_EXPERIMENT_WEB_REQUEST,

    /**
     * Calculates experiment final status
     */
    CALCULATE_EXPERIMENT_FINAL_STATUS
}
