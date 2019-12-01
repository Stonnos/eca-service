package com.ecaservice.report.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Experiment report model.
 *
 * @author Roman Batygin
 */
@Data
public class ExperimentBean {

    /**
     * Experiment uuid
     */
    private String uuid;

    /**
     * Experiment type
     */
    private String experimentType;

    /**
     * Experiment status
     */
    private String experimentStatus;

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
    private LocalDateTime creationDate;

    /**
     * Experiment processing start date
     */
    private LocalDateTime startDate;

    /**
     * Experiment processing end date
     */
    private LocalDateTime endDate;

    /**
     * Date when experiment results is sent
     */
    private LocalDateTime sentDate;

    /**
     * Experiment files deleted date
     */
    private LocalDateTime deletedDate;
}
