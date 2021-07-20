package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Classifier options response dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "ERS classifier options response model")
public class ClassifierOptionsResponseDto {

    /**
     * Classifier name
     */
    @Schema(description = "Classifier name")
    private String classifierName;

    /**
     * Classifier options config
     */
    @Schema(description = "Classifier input options json config")
    private String options;
}
