package com.ecaservice.model.options;

import eca.neural.functions.ActivationFunctionType;
import lombok.Data;

/**
 * Activation function options.
 *
 * @author Roman Batygin
 */
@Data
public class ActivationFunctionOptions {

    /**
     * Activation function type
     */
    private ActivationFunctionType activationFunctionType;

    /**
     * Activation function coefficient value
     */
    private Double coefficient;
}
