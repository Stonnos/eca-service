package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Attributes contingency table report dto.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Attributes contingency table report model")
public class ContingencyTableReportDto {

    /**
     * X attribute
     */
    @Schema(description = "X attribute")
    private AttributeDto xAttribute;

    /**
     * Y-axis attribute
     */
    @Schema(description = "Y attribute")
    private AttributeDto yAttribute;

    /**
     * Contingency table data
     */
    @Schema(description = "Contingency table data")
    private List<List<Integer>> tableData;

    /**
     * Chi squared calculated value
     */
    @Schema(description = "Chi squared calculated value", example = "10")
    private BigDecimal chiSquaredValue;

    /**
     * Chi squared critical value for specified significant level
     */
    @Schema(description = "Chi squared critical value for specified significant level", example = "15")
    private BigDecimal chiSquaredCriticalValue;

    /**
     * Degrees number
     */
    @Schema(description = "Degrees number", example = "2")
    private Integer df;

    /**
     * Significant level (alpha value)
     */
    @Schema(description = "Significant level (alpha value)", example = "0.05")
    private BigDecimal alpha;

    /**
     * Is there a statistical relationship between attributes?
     */
    @Schema(description = "Is there a statistical relationship between attributes?")
    private boolean significant;
}
