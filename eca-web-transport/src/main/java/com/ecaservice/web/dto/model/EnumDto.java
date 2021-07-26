package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Schema(description = "Enum value", required = true)
    private String value;

    /**
     * Enum value description
     */
    @Schema(description = "Enum value description", required = true)
    private String description;
}
