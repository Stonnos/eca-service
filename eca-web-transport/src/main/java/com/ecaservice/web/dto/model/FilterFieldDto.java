package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_INTEGER_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.ZERO_VALUE_STRING;

/**
 * Filter field dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Filter field model")
public class FilterFieldDto {

    /**
     * Field name
     */
    @Schema(description = "Field name", example = "field", maxLength = MAX_LENGTH_255)
    private String fieldName;

    /**
     * Field description
     */
    @Schema(description = "Field description", example = "Field description", maxLength = MAX_LENGTH_255)
    private String description;

    /**
     * Field order
     */
    @Schema(description = "Field order", example = "0", minimum = ZERO_VALUE_STRING, maximum = MAX_INTEGER_VALUE_STRING)
    private int fieldOrder;

    /**
     * Filter type
     */
    @Schema(description = "Filter field type", example = "TEXT", maxLength = MAX_LENGTH_255)
    private FilterFieldType filterFieldType;

    /**
     * Filter match mode
     */
    @Schema(description = "Filter match mode", example = "LIKE", maxLength = MAX_LENGTH_255)
    private MatchMode matchMode;

    /**
     * Allow multiple values
     */
    @Schema(description = "Allow multiple values")
    private boolean multiple;

    /**
     * Filter dictionary
     */
    @Schema(description = "Filter dictionary")
    private FilterDictionaryDto dictionary;
}
