package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_INTEGER_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_100_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_2_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.ZERO_VALUE_STRING;

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
    @Schema(description = "Test instances number", example = "150", minimum = VALUE_2_STRING,
            maximum = MAX_INTEGER_VALUE_STRING)
    private BigInteger numTestInstances;

    /**
     * Correctly classified instances number
     */
    @Schema(description = "Correctly classified instances number", example = "146", minimum = VALUE_2_STRING,
            maximum = MAX_INTEGER_VALUE_STRING)
    private BigInteger numCorrect;

    /**
     * Incorrectly classified instances number
     */
    @Schema(description = "Incorrectly classified instances number", example = "4", minimum = VALUE_2_STRING,
            maximum = MAX_INTEGER_VALUE_STRING)
    private BigInteger numIncorrect;

    /**
     * Correctly classified percentage
     */
    @Schema(description = "Correctly classified percentage", example = "96", minimum = ZERO_VALUE_STRING,
            maximum = VALUE_100_STRING)
    private BigDecimal pctCorrect;

    /**
     * Incorrectly classified percentage
     */
    @Schema(description = "Incorrectly classified percentage", example = "4", minimum = ZERO_VALUE_STRING,
            maximum = VALUE_100_STRING)
    private BigDecimal pctIncorrect;

    /**
     * Mean absolute error
     */
    @Schema(description = "Mean absolute error", example = "0.29", minimum = ZERO_VALUE_STRING,
            maximum = VALUE_1_STRING)
    private BigDecimal meanAbsoluteError;

    /**
     * Root mean squared error
     */
    @Schema(description = "Root mean squared error", example = "0.01", minimum = ZERO_VALUE_STRING,
            maximum = VALUE_1_STRING)
    private BigDecimal rootMeanSquaredError;

    /**
     * Max AUC value
     */
    @Schema(description = "Max AUC value", example = "0.89", minimum = ZERO_VALUE_STRING, maximum = VALUE_1_STRING)
    private BigDecimal maxAucValue;

    /**
     * Variance error
     */
    @Schema(description = "Variance error", example = "0.0012", minimum = ZERO_VALUE_STRING, maximum = VALUE_1_STRING)
    private BigDecimal varianceError;

    /**
     * 95% confidence interval lower bound value
     */
    @Schema(description = "95% confidence interval lower bound value", example = "0.01")
    private BigDecimal confidenceIntervalLowerBound;

    /**
     * 95% confidence interval upper bound value
     */
    @Schema(description = "95% confidence interval upper bound value", example = "0.035")
    private BigDecimal confidenceIntervalUpperBound;
}
