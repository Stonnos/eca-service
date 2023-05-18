package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LONG_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1_STRING;

/**
 * Attribute value dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Attribute value model")
public class AttributeValueDto {

    /**
     * ID
     */
    @Schema(description = "ID", example = "1", minimum = VALUE_1_STRING, maximum = MAX_LONG_VALUE_STRING)
    private Long id;

    /**
     * Attribute value
     */
    @Schema(description = "Attribute value", example = "attr_value", maxLength = MAX_LENGTH_255)
    private String value;
}
