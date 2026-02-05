package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * Attributes scatter plot.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Attributes scatter plot model")
public class AttributesScatterPlotDto {

    /**
     * X-axis attribute
     */
    @Schema(description = "X-axis attribute")
    private AttributeDto xAxisAttribute;

    /**
     * Y-axis attribute
     */
    @Schema(description = "Y-axis attribute")
    private AttributeDto yAxisAttribute;

    /**
     * Scatter plot data sets
     */
    @Schema(description = "Scatter plot data sets")
    private List<AttributesScatterPlotDataSetDto> dataSets;
}
