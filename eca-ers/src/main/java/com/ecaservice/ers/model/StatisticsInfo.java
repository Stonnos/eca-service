package com.ecaservice.ers.model;

import lombok.Data;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

/**
 * Evaluation statistics info model.
 *
 * @author Roman Batygin
 */
@Data
@Embeddable
public class StatisticsInfo {

    /**
     * Number of test instances
     */
    private Integer numTestInstances;

    /**
     * Correctly classified instances number
     */
    private Integer numCorrect;

    /**
     * Incorrectly classified instances number
     */
    private Integer numIncorrect;

    /**
     * Correctly classified instances percent
     */
    private BigDecimal pctCorrect;

    /**
     * Incorrectly classified instances percent
     */
    private BigDecimal pctIncorrect;

    /**
     * Classification mean absolute error
     */
    private BigDecimal meanAbsoluteError;

    /**
     * Classification root mean squared error
     */
    private BigDecimal rootMeanSquaredError;

    /**
     * Maximum AUC value
     */
    private BigDecimal maxAucValue;

    /**
     * Classifier variance error
     */
    private BigDecimal varianceError;

    /**
     * Classifier error Student confidence interval lower bound
     */
    private BigDecimal confidenceIntervalLowerBound;

    /**
     * Classifier error Student confidence interval upper bound
     */
    private BigDecimal confidenceIntervalUpperBound;
}
