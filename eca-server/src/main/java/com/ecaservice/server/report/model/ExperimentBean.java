package com.ecaservice.server.report.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Experiment report model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExperimentBean extends EvaluationBean {

    /**
     * Experiment type
     */
    private String experimentType;

    /**
     * User name
     */
    private String createdBy;

    /**
     * Experiment file
     */
    private String experimentPath;

    /**
     * Experiment files deleted date
     */
    private String deletedDate;
}
