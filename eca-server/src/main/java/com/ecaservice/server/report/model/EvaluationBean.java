package com.ecaservice.server.report.model;

import lombok.Data;

/**
 * Abstract evaluation bean.
 *
 * @author Roman Batygin
 */
@Data
public abstract class EvaluationBean {

    /**
     * Request unique identifier
     */
    private String requestId;

    /**
     * Training data name
     */
    private String relationName;

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
     * Request status
     */
    private String requestStatus;

    /**
     * Evaluation method
     */
    private String evaluationMethod;

    /**
     * Model evaluation total time
     */
    private String evaluationTotalTime;
}
