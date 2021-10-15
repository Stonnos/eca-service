package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Experiment progress dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Experiment progress model")
public class ExperimentProgressDto {

    /**
     * Is experiment processing finished?
     */
    @Schema(description = "Is experiment processing finished?")
    private boolean finished;

    /**
     * Experiment progress bar value
     */
    @Schema(description = "Experiment progress bar value", example = "85")
    private Integer progress;

    /**
     * Estimated time left
     */
    @Schema(description = "Estimated time left", example = "00:01:24")
    private String estimatedTimeLeft;
}
