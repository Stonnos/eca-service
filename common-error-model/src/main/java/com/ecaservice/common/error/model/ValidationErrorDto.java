package com.ecaservice.common.error.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.ecaservice.common.error.model.Constraints.STRING_MAX_LENGTH_1000;
import static com.ecaservice.common.error.model.Constraints.STRING_MAX_LENGTH_255;

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
    @Schema(description = "Field name", example = "field", maxLength = STRING_MAX_LENGTH_255)
    private String fieldName;

    /**
     * Error code
     */
    @Schema(description = "Error code", example = "NotNull", maxLength = STRING_MAX_LENGTH_255)
    private String code;

    /**
     * Error message
     */
    @Schema(description = "Error message", example = "Must be not null", maxLength = STRING_MAX_LENGTH_1000)
    private String errorMessage;
}
