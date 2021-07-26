package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Model for chart")
public class ChartDataDto {

    /**
     * Chart item name
     */
    @Schema(description = "Chart item name", required = true)
    private String name;

    /**
     * Chart item label
     */
    @Schema(description = "Chart item label", required = true)
    private String label;

    /**
     * Chart item value
     */
    @Schema(description = "Chart item value", required = true)
    private Long count;
}
