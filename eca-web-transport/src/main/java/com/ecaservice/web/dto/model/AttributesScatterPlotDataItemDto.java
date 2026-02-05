package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;

/**
 * Attributes scatter plot data item model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Attributes scatter plot data item model")
public class AttributesScatterPlotDataItemDto {

    /**
     * X attribute label
     */
    @Schema(description = "X attribute label", example = "Iris-setosa", maxLength = MAX_LENGTH_255)
    private String xLabel;

    /**
     * Y attribute label
     */
    @Schema(description = "Y attribute label", example = "Iris-setosa", maxLength = MAX_LENGTH_255)
    private String yLabel;

    /**
     * X value
     */
    @Schema(description = "X value", example = "10")
    private BigDecimal xValue;

    /**
     * Y value
     */
    @Schema(description = "Y value", example = "5")
    private BigDecimal yValue;

    /**
     * Class value
     */
    @Schema(description = "Class value", example = "Iris-setosa", maxLength = MAX_LENGTH_255)
    private String classValue;
}
