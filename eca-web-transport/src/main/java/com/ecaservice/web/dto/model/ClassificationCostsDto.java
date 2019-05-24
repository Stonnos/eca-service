package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Classification costs dto.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Classification costs model")
public class ClassificationCostsDto {

    /**
     * Class value
     */
    @ApiModelProperty(value = "Class value")
    private String classValue;

    /**
     * TP rate
     */
    @ApiModelProperty(value = "TP rate")
    private BigDecimal truePositiveRate;

    /**
     * FP rate
     */
    @ApiModelProperty(value = "FP rate")
    private BigDecimal falsePositiveRate;

    /**
     * TN rate
     */
    @ApiModelProperty(value = "TN rate")
    private BigDecimal trueNegativeRate;

    /**
     * FN rate
     */
    @ApiModelProperty(value = "FN rate")
    private BigDecimal falseNegativeRate;

    /**
     * AUC value
     */
    @ApiModelProperty(value = "AUC value")
    private BigDecimal aucValue;
}
