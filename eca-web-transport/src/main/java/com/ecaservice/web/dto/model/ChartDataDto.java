package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto model for charts.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Model for chart")
public class ChartDataDto {

    /**
     * Chart item label
     */
    @ApiModelProperty(value = "Chart item label", required = true)
    private String label;

    /**
     * Chart item value
     */
    @ApiModelProperty(value = "CHart item value", required = true)
    private Long count;
}
