package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create experiment result dto model.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Create experiment result model")
public class CreateExperimentResultDto {

    /**
     * Experiment request id
     */
    @Schema(description = "Experiment request id", required = true)
    private String requestId;

    /**
     * Is experiment created?
     */
    @Schema(description = "Experiment creation boolean flag", required = true)
    private boolean created;

    /**
     * Error message
     */
    @Schema(description = "Error message")
    private String errorMessage;
}
