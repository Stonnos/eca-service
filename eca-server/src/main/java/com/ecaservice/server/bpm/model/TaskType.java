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
     * Process experiment
     */
    PROCESS_EXPERIMENT,

    /**
     * Cancel experiment
     */
    CANCEL_EXPERIMENT,

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
     * Sent evaluation web push notification
     */
    SENT_EVALUATION_WEB_PUSH,

    /**
     * Sent experiment response to MQ
     */
    SENT_EXPERIMENT_RESPONSE,

    /**
     * Sent evaluation response to MQ
     */
    SENT_EVALUATION_RESPONSE,

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
     * Calculates experiment final status
     */
    CALCULATE_EXPERIMENT_FINAL_STATUS,

    /**
     * Create classifier evaluation web request
     */
    CREATE_EVALUATION_REQUEST,

    /**
     * Starts classifier evaluation
     */
    START_EVALUATION,

    /**
     * Process classifier evaluation
     */
    PROCESS_CLASSIFIER_EVALUATION,

    /**
     * Gets evaluation log details
     */
    GET_EVALUATION_DETAILS,

    /**
     * Set experiment request email
     */
    SET_EXPERIMENT_REQUEST_EMAIL,

    /**
     * Get optimal classifier options
     */
    GET_OPTIMAL_CLASSIFIER_OPTIONS,

    /**
     * Set optimal classifier options
     */
    SET_OPTIMAL_CLASSIFIER_OPTIONS,

    /**
     * Optimal classifier option error handler
     */
    OPTIMAL_CLASSIFIER_OPTIONS_ERROR_HANDLER,

    /**
     * Gets user profile options
     */
    GET_USER_PROFILE_OPTIONS
}
