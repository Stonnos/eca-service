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

    private ActivationFunctionType activationFunctionType;

    private Double coefficient;
}
