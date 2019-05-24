package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Roc - curve point dto.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Roc - curve point model")
public class RocCurvePointDto {

    /**
     * X value (100 - Specificity)
     */
    @ApiModelProperty(value = "x value (100 - Specificity)")
    private BigDecimal xValue;

    /**
     * Y value (Sensitivity)
     */
    @ApiModelProperty(value = "y value (Sensitivity)")
    private BigDecimal yValue;
}
