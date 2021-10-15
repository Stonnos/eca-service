package com.ecaservice.common.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Validation error dto.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Validation error model")
public class ValidationErrorDto implements Serializable {

    /**
     * Field name
     */
    @Schema(description = "Field name", example = "field")
    private String fieldName;

    /**
     * Error code
     */
    @Schema(description = "Error code", example = "NotNull")
    private String code;

    /**
     * Error message
     */
    @Schema(description = "Error message", example = "Must be not null")
    private String errorMessage;
}
