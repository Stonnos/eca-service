package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Filter field dictionary value dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Filter dictionary value model")
public class FilterDictionaryValueDto {

    /**
     * Label string
     */
    @Schema(description = "Filter dictionary field label")
    private String label;

    /**
     * String value
     */
    @Schema(description = "Filter dictionary field value")
    private String value;
}
