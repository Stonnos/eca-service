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
     * Error task type
     */
    ERROR
}
