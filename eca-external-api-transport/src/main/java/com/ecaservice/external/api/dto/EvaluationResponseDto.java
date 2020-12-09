package com.ecaservice.external.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Evaluation response dto.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class EvaluationResponseDto {

    @Tolerate
    public EvaluationResponseDto() {
        //default constructor
    }

    /**
     * Evaluation request id
     */
    @ApiModelProperty(value = "Evaluation request id")
    private String requestId;

    /**
     * Classifier model url
     */
    @ApiModelProperty(value = "Model url")
    private String modelUrl;

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
}
