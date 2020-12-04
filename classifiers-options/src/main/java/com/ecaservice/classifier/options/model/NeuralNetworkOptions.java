package com.ecaservice.classifier.options.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Neural network input options.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NeuralNetworkOptions extends ClassifierOptions {

    /**
     * Neurons number in input layer
     */
    private Integer numInNeurons;

    /**
     * Neurons number in output layer
     */
    private Integer numOutNeurons;

    /**
     * Hidden layer structure
     */
    private String hiddenLayer;

    /**
     * Seed value for random generator
     */
    private Integer seed;

    /**
     * Max. its for learning
     */
    private Integer numIterations;

    /**
     * Min. error for optimization algorithm
     */
    private Double minError;

    /**
     * Activation function options
     */
    private ActivationFunctionOptions activationFunctionOptions;

    /**
     * Back propagation options
     */
    private BackPropagationOptions backPropagationOptions;

}
