package com.ecaservice.report.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Evaluation log report model.
 *
 * @author Roman Batygin
 */
@Data
public class EvaluationBean {

    /**
     * Request unique identifier
     */
    private String requestId;

    /**
     * Request creation date
     */
    private LocalDateTime creationDate;

    /**
     * Evaluation start date
     */
    private LocalDateTime startDate;

    /**
     * Evaluation end date
     */
    private LocalDateTime endDate;

    /**
     * Classifier name
     */
    private String classifierName;

    /**
     * Evaluation status
     */
    private String evaluationStatus;

    /**
     * Evaluation method
     */
    private String evaluationMethod;

    /**
     * Folds number for k * V cross - validation method
     */
    private Integer numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    private Integer numTests;

    /**
     * Seed value for k * V cross - validation method
     */
    private Integer seed;

    /**
     * Training data name
     */
    private String relationName;
}
