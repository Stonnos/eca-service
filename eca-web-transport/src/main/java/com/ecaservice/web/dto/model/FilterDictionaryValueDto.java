package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;

/**
 * Filter field dictionary value dto model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Filter dictionary value model")
public class FilterDictionaryValueDto {

    /**
     * Label string
     */
    @Schema(description = "Filter dictionary field label", example = "Label value", maxLength = MAX_LENGTH_255)
    private String label;

    /**
     * String value
     */
    @Schema(description = "Filter dictionary field value", example = "Value code", maxLength = MAX_LENGTH_255)
    private String value;
}
