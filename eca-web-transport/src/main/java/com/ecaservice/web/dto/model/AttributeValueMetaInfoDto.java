package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_INTEGER_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.ZERO_VALUE_STRING;

/**
 * Attribute value meta info dto model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Attribute value meta info model")
public class AttributeValueMetaInfoDto {

    /**
     * Attribute value index
     */
    @Schema(description = "Attribute value index", example = "0", minimum = ZERO_VALUE_STRING,
            maximum = MAX_INTEGER_VALUE_STRING)
    private int index;

    /**
     * Attribute value
     */
    @Schema(description = "Attribute value", example = "attr_value", maxLength = MAX_LENGTH_255)
    private String value;
}
