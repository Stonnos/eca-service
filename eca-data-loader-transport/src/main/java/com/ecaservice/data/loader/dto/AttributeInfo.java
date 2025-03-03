package com.ecaservice.data.loader.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

import static com.ecaservice.data.loader.dto.FieldConstraints.MAX_LENGTH_255;

/**
 * Attribute model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Attribute info")
public class AttributeInfo {

    /**
     * Attribute name
     */
    @Schema(description = "Attribute name", example = "age", maxLength = MAX_LENGTH_255)
    private String name;

    /**
     * Attribute type
     */
    @Schema(description = "Attribute type", maxLength = MAX_LENGTH_255)
    private AttributeInfoType type;

    /**
     * Date format for date attribute
     */
    @Schema(description = "Date format for date attribute", example = "yyyy-MM-dd HH:mm:ss", maxLength = MAX_LENGTH_255)
    private String dateFormat;

    /**
     * Nominal attribute values
     */
    @Schema(description = "Nominal attribute values")
    private List<String> values;
}
