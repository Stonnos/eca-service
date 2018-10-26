package com.ecaservice.model.options;

import lombok.Data;

/**
 * Back propagation learning algorithm options model.
 *
 * @author Roman Batygin
 */
@Data
public class BackPropagationOptions {

    /**
     * Learning rate value
     */
    private Double learningRate;

    /**
     * Momentum coefficient value
     */
    private Double momentum;
}
