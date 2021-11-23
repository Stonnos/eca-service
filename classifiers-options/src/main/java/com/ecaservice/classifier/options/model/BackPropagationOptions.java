package com.ecaservice.classifier.options.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Back propagation learning algorithm options model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Back propagation learning algorithm options")
public class BackPropagationOptions {

    /**
     * Learning rate value
     */
    @Schema(description = "Learning rate value")
    private Double learningRate;

    /**
     * Momentum coefficient value
     */
    @Schema(description = "Momentum coefficient value")
    private Double momentum;
}
