package com.ecaservice.auto.test.model.evaluation;

import com.ecaservice.test.common.model.MatchResult;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Classification results costs details match.
 *
 * @author Roman Batygin
 */
@Data
public class ClassificationCostsDetailsMatch {

    /**
     * Expected class value
     */
    private String expectedClassValue;

    /**
     * Actual class value
     */
    private String actualClassValue;

    /**
     * Class value match result
     */
    private MatchResult classValueMatchResult;

    /**
     * Expected TP rate
     */
    private BigDecimal expectedTruePositiveRate;

    /**
     * Actual TP rate
     */
    private BigDecimal actualTruePositiveRate;

    /**
     * TP rate match result
     */
    private MatchResult truePositiveRateMatchResult;

    /**
     * Expected FP rate
     */
    private BigDecimal expectedFalsePositiveRate;

    /**
     * Actual FP rate
     */
    private BigDecimal actualFalsePositiveRate;

    /**
     * FP rate match result
     */
    private MatchResult falsePositiveRateMatchResult;

    /**
     * Expected TN rate
     */
    private BigDecimal expectedTrueNegativeRate;

    /**
     * Actual TN rate
     */
    private BigDecimal actualTrueNegativeRate;

    /**
     * TN rate match result
     */
    private MatchResult trueNegativeRateMatchResult;

    /**
     * Expected FN rate
     */
    private BigDecimal expectedFalseNegativeRate;

    /**
     * Actual FN rate
     */
    private BigDecimal actualFalseNegativeRate;

    /**
     * FN rate match result
     */
    private MatchResult falseNegativeRateMatchResult;

    /**
     * Expected AUC value
     */
    private BigDecimal expectedAucValue;

    /**
     * Actual AUC value
     */
    private BigDecimal actualAucValue;

    /**
     * AUC value match result
     */
    private MatchResult aucValueMatchResult;
}
