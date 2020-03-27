package com.ecaservice.report.model;

import lombok.Data;

/**
 * Experiment report model.
 *
 * @author Roman Batygin
 */
@Data
public class ExperimentBean {

    /**
     * Request id
     */
    private String requestId;

    /**
     * Experiment type
     */
    private String experimentType;

    /**
     * Request status
     */
    private String requestStatus;

    /**
     * Evaluation method
     */
    private String evaluationMethod;

    /**
     * First name
     */
    private String firstName;

    /**
     * Email
     */
    private String email;

    /**
     * Experiment file absolute path
     */
    private String experimentAbsolutePath;

    /**
     * Training data absolute path
     */
    private String trainingDataAbsolutePath;

    /**
     * Request creation date
     */
    private String creationDate;

    /**
     * Experiment processing start date
     */
    private String startDate;

    /**
     * Experiment processing end date
     */
    private String endDate;

    /**
     * Date when experiment results is sent
     */
    private String sentDate;

    /**
     * Experiment files deleted date
     */
    private String deletedDate;
}
