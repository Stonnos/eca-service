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
     * User name
     */
    private String createdBy;

    /**
     * Training data name
     */
    private String relationName;

    /**
     * Request creation date
     */
    private String creationDate;

    /**
     * Request start date
     */
    private String startDate;

    /**
     * Request end date
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

    /**
     * Model file
     */
    private String modelPath;

    /**
     * Model deleted date
     */
    private String deletedDate;
}
