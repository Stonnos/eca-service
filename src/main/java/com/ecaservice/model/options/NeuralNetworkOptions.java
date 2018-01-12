package com.ecaservice.model.options;

import lombok.Data;

/**
 * Neural network input options.
 *
 * @author Roman Batygin
 */
@Data
public class NeuralNetworkOptions extends ClassifierOptions {

    private ActivationFunctionOptions activationFunctionOptions;

    private Integer maximumFractionDigits;
}
