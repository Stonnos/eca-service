package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * Create experiment result dto model.
 *
 * @author Roman Batygin
 */
@Data
@Builder
@Schema(description = "Create experiment result model")
public class CreateExperimentResultDto {

    /**
     * Experiment id
     */
    @Schema(description = "Experiment id", required = true)
    private Long id;

    /**
     * Request id
     */
    @Schema(description = "Request id", required = true)
    private String requestId;
}
