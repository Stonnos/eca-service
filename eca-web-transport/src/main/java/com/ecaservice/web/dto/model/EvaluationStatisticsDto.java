package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Evaluation statistics dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Classifier evaluation statistics model")
public class EvaluationStatisticsDto {

    /**
     * Test instances number
     */
    @Schema(description = "Test instances number")
    private BigInteger numTestInstances;

    /**
     * Correctly classified instances number
     */
    @Schema(description = "Correctly classified instances number")
    private BigInteger numCorrect;

    /**
     * Incorrectly classified instances number
     */
    @Schema(description = "Incorrectly classified instances number")
    private BigInteger numIncorrect;

    /**
     * Correctly classified percentage
     */
    @Schema(description = "Correctly classified percentage")
    private BigDecimal pctCorrect;

    /**
     * Incorrectly classified percentage
     */
    @Schema(description = "Incorrectly classified percentage")
    private BigDecimal pctIncorrect;

    /**
     * Mean absolute error
     */
    @Schema(description = "Mean absolute error")
    private BigDecimal meanAbsoluteError;

    /**
     * Root mean squared error
     */
    @Schema(description = "Root mean squared error")
    private BigDecimal rootMeanSquaredError;

    /**
     * Max AUC value
     */
    @Schema(description = "Max AUC value")
    private BigDecimal maxAucValue;

    /**
     * Variance error
     */
    @Schema(description = "Variance error")
    private BigDecimal varianceError;

    /**
     * 95% confidence interval lower bound value
     */
    @Schema(description = "95% confidence interval lower bound value")
    private BigDecimal confidenceIntervalLowerBound;

    /**
     * 95% confidence interval upper bound value
     */
    @Schema(description = "95% confidence interval upper bound value")
    private BigDecimal confidenceIntervalUpperBound;
}
