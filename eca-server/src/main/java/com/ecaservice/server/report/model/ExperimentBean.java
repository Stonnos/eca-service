package com.ecaservice.server.report.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

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
     * The best classifier correctly classified percentage
     */
    private BigDecimal maxPctCorrect;
}
