package com.ecaservice.classifier.options.model;

import eca.neural.functions.ActivationFunctionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.io.Serializable;

import static com.ecaservice.classifier.options.model.Constraints.DECIMAL_VALUE_0_STRING;
import static com.ecaservice.classifier.options.model.Constraints.INTEGER_MAX_VALUE_STRING;
import static com.ecaservice.classifier.options.model.Constraints.MAX_LENGTH_255;

/**
 * Activation function options.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Activation function options")
public class ActivationFunctionOptions implements Serializable {

    /**
     * Activation function type
     */
    @Schema(description = "Activation function type", maxLength = MAX_LENGTH_255)
    private ActivationFunctionType activationFunctionType;

    /**
     * Activation function coefficient value
     */
    @DecimalMin(value = DECIMAL_VALUE_0_STRING, inclusive = false)
    @DecimalMax(value = INTEGER_MAX_VALUE_STRING, inclusive = false)
    @Schema(description = "Activation function coefficient value")
    private Double coefficient;
}
