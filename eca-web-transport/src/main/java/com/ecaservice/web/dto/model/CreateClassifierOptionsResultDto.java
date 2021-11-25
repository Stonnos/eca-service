package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LONG_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1_STRING;

/**
 * Create classifier options dto model.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Create classifier options model")
public class CreateClassifierOptionsResultDto {

    /**
     * Classifier options id
     */
    @Schema(description = "Classifier options id", required = true, example = "1", minimum = VALUE_1_STRING,
            maximum = MAX_LONG_VALUE_STRING)
    private Long id;

    /**
     * Source file name
     */
    @Schema(description = "Source file name", required = true, example = "cart.json", maxLength = MAX_LENGTH_255)
    private String sourceFileName;

    /**
     * Is classifier options successfully saved?
     */
    @Schema(description = "Classifier options saved boolean flag", required = true)
    private Boolean success;

    /**
     * Error message
     */
    @Schema(description = "Error message", maxLength = MAX_LENGTH_255)
    private String errorMessage;
}
