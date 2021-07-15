package com.ecaservice.ers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;

import static com.ecaservice.ers.dto.Constraints.DECIMAL_MAX_100;
import static com.ecaservice.ers.dto.Constraints.DECIMAL_MAX_ONE;
import static com.ecaservice.ers.dto.Constraints.DECIMAL_MIN_ZERO;
import static com.ecaservice.ers.dto.Constraints.MIN_2;
import static com.ecaservice.ers.dto.Constraints.MIN_ZERO;

/**
 * Evaluation statistics report model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Evaluation statistics report model")
public class StatisticsReport {

    /**
     * Test instances number
     */
    @NotNull
    @Min(MIN_2)
    @Schema(description = "Test instances number", example = "100", required = true)
    private BigInteger numTestInstances;

    /**
     * Correctly classified instances number
     */
    @NotNull
    @Min(MIN_ZERO)
    @Schema(description = "Correctly classified instances number", example = "100", required = true)
    private BigInteger numCorrect;

    /**
     * Incorrectly classified instances number
     */
    @NotNull
    @Min(MIN_ZERO)
    @Schema(description = "Incorrectly classified instances number", example = "0", required = true)
    private BigInteger numIncorrect;

    /**
     * Correctly classified percentage
     */
    @NotNull
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_100)
    @Schema(description = "Correctly classified percentage", example = "100.0", required = true)
    private BigDecimal pctCorrect;

    /**
     * Incorrectly classified percentage
     */
    @NotNull
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_100)
    @Schema(description = "Incorrectly classified percentage", example = "0.0", required = true)
    private BigDecimal pctIncorrect;

    /**
     * Mean absolute error
     */
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_ONE)
    @Schema(description = "Mean absolute error", example = "0.0")
    private BigDecimal meanAbsoluteError;

    /**
     * Root mean squared error
     */
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_ONE)
    @Schema(description = "Root mean squared error", example = "0.0")
    private BigDecimal rootMeanSquaredError;

    /**
     * Max AUC value
     */
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_ONE)
    @Schema(description = "Max AUC value", example = "1.0")
    private BigDecimal maxAucValue;

    /**
     * Variance error
     */
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_ONE)
    @Schema(description = "Variance error", example = "0.0")
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
