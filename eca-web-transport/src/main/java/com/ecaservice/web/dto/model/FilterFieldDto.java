package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

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
    @Schema(description = "Field name", example = "field")
    private String fieldName;

    /**
     * Field description
     */
    @Schema(description = "Field description", example = "Field sescription")
    private String description;

    /**
     * Field order
     */
    @Schema(description = "Field order", example = "0")
    private int fieldOrder;

    /**
     * Filter type
     */
    @Schema(description = "Filter field type", example = "TEXT")
    private FilterFieldType filterFieldType;

    /**
     * Filter match mode
     */
    @Schema(description = "Filter match mode", example = "LIKE")
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
