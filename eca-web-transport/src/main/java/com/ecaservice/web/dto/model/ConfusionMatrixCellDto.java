package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Confusion matrix cell dto.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Confusion matrix cell model")
public class ConfusionMatrixCellDto {

    /**
     * Instances number
     */
    @Schema(description = "Instances number", example = "2")
    private int numInstances;

    /**
     * Cell state
     */
    @Schema(description = "Cell state")
    private ConfusionMatrixCellState state;
}
