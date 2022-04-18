package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;

/**
 * Field dictionary value dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Field dictionary value model")
public class FieldDictionaryValueDto {

    /**
     * Label string
     */
    @Schema(description = "Field dictionary field label", example = "Label value", maxLength = MAX_LENGTH_255)
    private String label;

    /**
     * String value
     */
    @Schema(description = "Field dictionary field value", example = "Value code", maxLength = MAX_LENGTH_255)
    private String value;
}
