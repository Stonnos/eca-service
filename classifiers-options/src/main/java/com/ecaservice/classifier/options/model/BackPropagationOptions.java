package com.ecaservice.classifier.options.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

import static com.ecaservice.classifier.options.model.Constraints.DECIMAL_VALUE_0_STRING;
import static com.ecaservice.classifier.options.model.Constraints.DECIMAL_VALUE_1_STRING;

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
    @DecimalMin(value = DECIMAL_VALUE_0_STRING, inclusive = false)
    @DecimalMax(value = DECIMAL_VALUE_1_STRING)
    @Schema(description = "Learning rate value")
    private Double learningRate;

    /**
     * Momentum coefficient value
     */
    @DecimalMin(value = DECIMAL_VALUE_0_STRING)
    @DecimalMax(value = DECIMAL_VALUE_1_STRING, inclusive = false)
    @Schema(description = "Momentum coefficient value")
    private Double momentum;
}
