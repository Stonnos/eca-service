package com.ecaservice.ers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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
    @Schema(description = "Classifier name", example = "CART", required = true)
    private String classifierName;

    /**
     * Classifier description
     */
    @Size(max = MAX_LENGTH_255)
    @Schema(description = "Classifier description")
    private String classifierDescription;

    /**
     * Classifier options string
     */
    @NotBlank
    @Size(min = MIN_1)
    @Schema(description = "Classifier options string", example = "classifier options string", required = true)
    private String options;
}
