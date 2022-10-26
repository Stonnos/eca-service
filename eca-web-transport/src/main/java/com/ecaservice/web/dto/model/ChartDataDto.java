package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LONG_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.ZERO_VALUE_STRING;

/**
 * Chart data dto.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Chart data model")
public class ChartDataDto {

    /**
     * Chart item name
     */
    @Schema(description = "Chart item name", required = true, example = "key", maxLength = MAX_LENGTH_255)
    private String name;

    /**
     * Chart item label
     */
    @Schema(description = "Chart item label", required = true, example = "label value", maxLength = MAX_LENGTH_255)
    private String label;

    /**
     * Chart item value
     */
    @Schema(description = "Chart item value", required = true, example = "10", minimum = ZERO_VALUE_STRING,
            maximum = MAX_LONG_VALUE_STRING)
    private Long count;
}
