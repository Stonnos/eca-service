package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Schema(description = "Input option name")
    private String optionName;

    /**
     * Option value
     */
    @Schema(description = "Input option value")
    private String optionValue;
}
