package com.ecaservice.report.model;

import lombok.Data;

/**
 * Classifier options request dto model.
 *
 * @author Roman Batygin
 */
@Data
public class ClassifierOptionsRequestBean {

    /**
     * ERS - service request date
     */
    private String requestDate;

    /**
     * Request id
     */
    private String requestId;

    /**
     * Training data name
     */
    private String relationName;

    /**
     * Evaluation method
     */
    private String evaluationMethod;

    /**
     * Response status from ERS - service
     */
    private String responseStatus;

    /**
     * Optimal classifier name
     */
    private String classifierName;

    /**
     * Optimal classifier options
     */
    private String optimalClassifierOptions;
}
