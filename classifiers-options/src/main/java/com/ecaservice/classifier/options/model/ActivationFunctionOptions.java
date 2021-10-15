package com.ecaservice.classifier.options.model;

import eca.neural.functions.ActivationFunctionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * Activation function options.
 *
 * @author Roman Batygin
 */
@Data
public class ActivationFunctionOptions implements Serializable {

    /**
     * Activation function type
     */
    @Schema(description = "Activation function type")
    private ActivationFunctionType activationFunctionType;

    /**
     * Activation function coefficient value
     */
    @Schema(description = "Activation function coefficient value")
    private Double coefficient;
}
