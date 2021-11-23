package com.ecaservice.classifier.options.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Neural network input options.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Neural network classifier options")
@EqualsAndHashCode(callSuper = true)
public class NeuralNetworkOptions extends ClassifierOptions {

    /**
     * Neurons number in input layer
     */
    @Schema(description = "Neurons number in input layer")
    private Integer numInNeurons;

    /**
     * Neurons number in output layer
     */
    @Schema(description = "Neurons number in output layer")
    private Integer numOutNeurons;

    /**
     * Hidden layer structure
     */
    @Schema(description = "Hidden layer structure")
    private String hiddenLayer;

    /**
     * Seed value for random generator
     */
    @Schema(description = "Seed value for random generator")
    private Integer seed;

    /**
     * Max. its for learning
     */
    @Schema(description = "Max. its for learning")
    private Integer numIterations;

    /**
     * Min. error for optimization algorithm
     */
    @Schema(description = "Min. error for optimization algorithm")
    private Double minError;

    /**
     * Activation function options
     */
    @Schema(description = "Activation function options")
    private ActivationFunctionOptions activationFunctionOptions;

    /**
     * Back propagation options
     */
    @Schema(description = "Back propagation options")
    private BackPropagationOptions backPropagationOptions;

}
