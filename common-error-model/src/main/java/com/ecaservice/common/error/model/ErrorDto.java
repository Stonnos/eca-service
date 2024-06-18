package com.ecaservice.common.error.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.ecaservice.common.error.model.Constraints.STRING_MAX_LENGTH_1000;
import static com.ecaservice.common.error.model.Constraints.STRING_MAX_LENGTH_255;

/**
 * Error dto.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error model")
public class ErrorDto implements Serializable {

    /**
     * Error code
     */
    @Schema(description = "Error code", example = "InternalError", maxLength = STRING_MAX_LENGTH_255)
    private String code;

    /**
     * Error message
     */
    @Schema(description = "Error message", example = "Internal error", maxLength = STRING_MAX_LENGTH_1000)
    private String errorMessage;
}
