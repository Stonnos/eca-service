package com.ecaservice.ers.report.model;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Classifier evaluation results history report model.
 *
 * @author Roman Batygin
 */
@Data
public class EvaluationResultsHistoryBean {

    /**
     * Classifier name
     */
    private String classifierName;

    /**
     * Training data name
     */
    private String relationName;

    /**
     * Evaluation method
     */
    private String evaluationMethod;

    /**
     * Classifier options
     */
    private String classifierOptions;

    /**
     * Save date
     */
    private String saveDate;

    /**
     * Test instances number
     */
    private BigInteger numTestInstances;

    /**
     * Correctly classified instances number
     */
    private BigInteger numCorrect;

    /**
     * Incorrectly classified instances number
     */
    private BigInteger numIncorrect;

    /**
     * Correctly classified percentage
     */
    private BigDecimal pctCorrect;

    /**
     * Incorrectly classified percentage
     */
    private BigDecimal pctIncorrect;

    /**
     * Mean absolute error
     */
    private BigDecimal meanAbsoluteError;

    /**
     * Root mean squared error
     */
    private BigDecimal rootMeanSquaredError;

    /**
     * Max AUC value
     */
    private BigDecimal maxAucValue;

    /**
     * Variance error
     */
    private BigDecimal varianceError;

    /**
     * 95% confidence interval lower bound value
     */
    private BigDecimal confidenceIntervalLowerBound;

    /**
     * 95% confidence interval upper bound value
     */
    private BigDecimal confidenceIntervalUpperBound;
}
