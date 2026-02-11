package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;

/**
 * Attributes scatter plot data set model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Attributes scatter plot data set model")
public class AttributesScatterPlotDataSetDto {

    /**
     * Class value
     */
    @Schema(description = "Class value", example = "Iris-setosa", maxLength = MAX_LENGTH_255)
    private String classValue;

    /**
     * Scatter plot data items
     */
    @Schema(description = "Scatter plot data items")
    private List<AttributesScatterPlotDataItemDto> items;
}
