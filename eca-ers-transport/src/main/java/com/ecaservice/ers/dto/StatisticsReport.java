package com.ecaservice.ers.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "Evaluation statistics report model")
public class StatisticsReport {

    /**
     * Test instances number
     */
    @NotNull
    @Min(MIN_2)
    @ApiModelProperty(value = "Test instances number", example = "100")
    private BigInteger numTestInstances;

    /**
     * Correctly classified instances number
     */
    @NotNull
    @Min(MIN_ZERO)
    @ApiModelProperty(value = "Correctly classified instances number", example = "100")
    private BigInteger numCorrect;

    /**
     * Incorrectly classified instances number
     */
    @NotNull
    @Min(MIN_ZERO)
    @ApiModelProperty(value = "Incorrectly classified instances number", example = "0")
    private BigInteger numIncorrect;

    /**
     * Correctly classified percentage
     */
    @NotNull
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_100)
    @ApiModelProperty(value = "Correctly classified percentage", example = "100.0")
    private BigDecimal pctCorrect;

    /**
     * Incorrectly classified percentage
     */
    @NotNull
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_100)
    @ApiModelProperty(value = "Incorrectly classified percentage", example = "0.0")
    private BigDecimal pctIncorrect;

    /**
     * Mean absolute error
     */
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_ONE)
    @ApiModelProperty(value = "Mean absolute error", example = "0.0")
    private BigDecimal meanAbsoluteError;

    /**
     * Root mean squared error
     */
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_ONE)
    @ApiModelProperty(value = "Root mean squared error", example = "0.0")
    private BigDecimal rootMeanSquaredError;

    /**
     * Max AUC value
     */
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_ONE)
    @ApiModelProperty(value = "Max AUC value", example = "1.0")
    private BigDecimal maxAucValue;

    /**
     * Variance error
     */
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_ONE)
    @ApiModelProperty(value = "Variance error", example = "0.0")
    private BigDecimal varianceError;

    /**
     * 95% confidence interval lower bound value
     */
    @ApiModelProperty(value = "95% confidence interval lower bound value")
    private BigDecimal confidenceIntervalLowerBound;

    /**
     * 95% confidence interval upper bound value
     */
    @ApiModelProperty(value = "95% confidence interval upper bound value")
    private BigDecimal confidenceIntervalUpperBound;
}
