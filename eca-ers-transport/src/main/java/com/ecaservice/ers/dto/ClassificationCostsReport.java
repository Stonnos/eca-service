package com.ecaservice.ers.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

import static com.ecaservice.ers.dto.Constraints.DECIMAL_MAX_ONE;
import static com.ecaservice.ers.dto.Constraints.DECIMAL_MIN_ZERO;
import static com.ecaservice.ers.dto.Constraints.MAX_LENGTH_255;

/**
 * Classification costs report model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Classification costs report model")
public class ClassificationCostsReport {

    /**
     * Class value
     */
    @NotBlank
    @Size(max = MAX_LENGTH_255)
    @ApiModelProperty(value = "Class value", example = "classValue")
    private String classValue;

    /**
     * True positive rate
     */
    @NotNull
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_ONE)
    @ApiModelProperty(value = "True positive rate", example = "0.75")
    private BigDecimal truePositiveRate;

    /**
     * False positive rate
     */
    @NotNull
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_ONE)
    @ApiModelProperty(value = "False positive rate", example = "0.25")
    private BigDecimal falsePositiveRate;

    /**
     * True negative rate
     */
    @NotNull
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_ONE)
    @ApiModelProperty(value = "True negative rate", example = "0.25")
    private BigDecimal trueNegativeRate;

    /**
     * False negative rate
     */
    @NotNull
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_ONE)
    @ApiModelProperty(value = "False negative rate", example = "0.5")
    private BigDecimal falseNegativeRate;

    /**
     * Roc curve report data
     */
    @Valid
    @ApiModelProperty(value = "Roc curve report data", allowEmptyValue = true)
    private RocCurveReport rocCurve;
}
