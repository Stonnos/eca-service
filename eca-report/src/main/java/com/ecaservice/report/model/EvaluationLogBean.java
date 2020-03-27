package com.ecaservice.report.model;

import lombok.Data;

/**
 * Evaluation log report model.
 *
 * @author Roman Batygin
 */
@Data
public class EvaluationLogBean {

    /**
     * Request unique identifier
     */
    private String requestId;

    /**
     * Request creation date
     */
    private String creationDate;

    /**
     * Evaluation start date
     */
    private String startDate;

    /**
     * Evaluation end date
     */
    private String endDate;

    /**
     * Classifier name
     */
    private String classifierName;

    /**
     * Request status
     */
    private String requestStatus;

    /**
     * Evaluation method
     */
    private String evaluationMethod;

    /**
     * Training data name
     */
    private String relationName;
}
