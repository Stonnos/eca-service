package com.ecaservice.web.dto.model;

/**
 * ERS report status enum.
 *
 * @author Roman Batygin
 */
public enum ErsReportStatus {

    /**
     * Experiment results has been sent to ERS service
     */
    SUCCESS_SENT,

    /**
     * Experiment finished with error or timeout
     */
    EXPERIMENT_ERROR,

    /**
     * Experiment results hasn't been sent to ERS service and deleted
     */
    EXPERIMENT_DELETED,

    /**
     * Experiment results needs sending to ERS service
     */
    NEED_SENT
}
