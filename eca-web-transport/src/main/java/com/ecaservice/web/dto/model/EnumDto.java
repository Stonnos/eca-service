package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;

/**
 * Enum dto model.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Enum dto model")
public class EnumDto {

    /**
     * Enum value
     */
    @Schema(description = "Enum value", required = true, example = "Enum code", maxLength = MAX_LENGTH_255)
    private String value;

    /**
     * Enum value description
     */
    @Schema(description = "Enum value description", required = true, example = "Enum value", maxLength = MAX_LENGTH_255)
    private String description;
}
