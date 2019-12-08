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
     * Chart item name
     */
    @ApiModelProperty(value = "Chart item name", required = true)
    private String name;

    /**
     * Chart item label
     */
    @ApiModelProperty(value = "Chart item label", required = true)
    private String label;

    /**
     * Chart item value
     */
    @ApiModelProperty(value = "Chart item value", required = true)
    private Long count;
}
