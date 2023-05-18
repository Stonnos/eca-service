package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_INTEGER_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LONG_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.ZERO_VALUE_STRING;

/**
 * Attribute dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Attribute model")
public class AttributeDto {

    /**
     * ID
     */
    @Schema(description = "ID", example = "1", minimum = VALUE_1_STRING, maximum = MAX_LONG_VALUE_STRING)
    private Long id;

    /**
     * Attribute name
     */
    @Schema(description = "Attribute name", example = "attr_name", maxLength = MAX_LENGTH_255)
    private String name;

    /**
     * Attribute index
     */
    @Schema(description = "Attribute index", example = "0", minimum = ZERO_VALUE_STRING, maximum =
            MAX_INTEGER_VALUE_STRING)
    private int index;

    /**
     * Is selected?
     */
    @Schema(description = "Is attribute selected?")
    private boolean selected;

    /**
     * Attribute type
     */
    @Schema(description = "Attribute type")
    private EnumDto type;

    /**
     * Attribute values list for nominal attribute
     */
    @ArraySchema(schema = @Schema(description = "Attribute values list for nominal attribute"))
    private List<AttributeValueDto> values;
}
