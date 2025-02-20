package com.ecaservice.ers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static com.ecaservice.ers.dto.Constraints.MAX_LENGTH_255;
import static com.ecaservice.ers.dto.Constraints.MIN_1;

/**
 * Classifier report model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Classifier report model")
public class ClassifierReport {

    /**
     * Classifier name
     */
    @NotBlank
    @Size(min = MIN_1, max = MAX_LENGTH_255)
    @Schema(description = "Classifier name", example = "CART", requiredMode = Schema.RequiredMode.REQUIRED)
    private String classifierName;

    /**
     * Classifier options string
     */
    @NotBlank
    @Size(min = MIN_1)
    @Schema(description = "Classifier options string", example = "classifier options string",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String options;
}
