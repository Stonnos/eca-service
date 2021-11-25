package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;

/**
 * Classifier input option dto model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Classifier input option model")
public class InputOptionDto {

    /**
     * Option key
     */
    @Schema(description = "Input option name", example = "Iterations number", maxLength = MAX_LENGTH_255)
    private String optionName;

    /**
     * Option value
     */
    @Schema(description = "Input option value", example = "100", maxLength = MAX_LENGTH_255)
    private String optionValue;
}
