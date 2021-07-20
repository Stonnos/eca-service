package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Schema(description = "Classifier options id", required = true)
    private Long id;

    /**
     * Source file name
     */
    @Schema(description = "Source file name", required = true)
    private String sourceFileName;

    /**
     * Is classifier options successfully saved?
     */
    @Schema(description = "Classifier options saved boolean flag", required = true)
    private Boolean success;

    /**
     * Error message
     */
    @Schema(description = "Error message")
    private String errorMessage;
}
