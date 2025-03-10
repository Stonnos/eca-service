package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_INTEGER_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.ZERO_VALUE_STRING;

/**
 * Attribute meta info dto model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Attribute meta info model")
public class AttributeMetaInfoDto {

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
     * Attribute type
     */
    @Schema(description = "Attribute type")
    private EnumDto type;

    /**
     * Date format for date attribute
     */
    @Schema(description = "Date format for date attribute", example = "yyyy-MM-dd HH:mm:ss", maxLength = MAX_LENGTH_255)
    private String dateFormat;

    /**
     * Attribute values list for nominal attribute
     */
    @ArraySchema(schema = @Schema(description = "Attribute values list for nominal attribute"))
    private List<AttributeValueMetaInfoDto> values;
}
