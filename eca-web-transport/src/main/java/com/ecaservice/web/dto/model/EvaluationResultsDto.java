package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Evaluation results dto model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Classifier evaluation results model")
public class EvaluationResultsDto {

    /**
     * Test instances number
     */
    @ApiModelProperty(value = "Test instances number")
    private BigInteger numTestInstances;

    /**
     * Correctly classified instances number
     */
    @ApiModelProperty(value = "Correctly classified instances number")
    private BigInteger numCorrect;

    /**
     * Incorrectly classified instances number
     */
    @ApiModelProperty(value = "Incorrectly classified instances number")
    private BigInteger numIncorrect;

    /**
     * Correctly classified percentage
     */
    @ApiModelProperty(value = "Correctly classified percentage")
    private BigDecimal pctCorrect;

    /**
     * Incorrectly classified percentage
     */
    @ApiModelProperty(value = "Incorrectly classified percentage")
    private BigDecimal pctIncorrect;

    /**
     * Mean absolute error
     */
    @ApiModelProperty(value = "Mean absolute error")
    private BigDecimal meanAbsoluteError;

    /**
     * Root mean squared error
     */
    @ApiModelProperty(value = "Root mean squared error")
    private BigDecimal rootMeanSquaredError;

    /**
     * Max AUC value
     */
    @ApiModelProperty(value = "Max AUC value")
    private BigDecimal maxAucValue;

    /**
     * Variance error
     */
    @ApiModelProperty(value = "Variance error")
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
