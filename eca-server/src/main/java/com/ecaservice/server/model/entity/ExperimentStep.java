package com.ecaservice.server.model.entity;

/**
 * Experiment step enum.
 *
 * @author Roman batygin
 */
public enum ExperimentStep {

    /**
     * Experiment model processing
     */
    EXPERIMENT_PROCESSING,

    /**
     * Creates ers report
     */
    CREATE_ERS_REPORT,

    /**
     * Upload experiment model to S3
     */
    UPLOAD_EXPERIMENT_MODEL,

    /**
     * Gets experiment download url
     */
    GET_EXPERIMENT_DOWNLOAD_URL
}
