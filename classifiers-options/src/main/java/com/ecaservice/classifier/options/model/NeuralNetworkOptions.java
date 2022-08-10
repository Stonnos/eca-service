package com.ecaservice.classifier.options.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.ecaservice.classifier.options.model.Constraints.DECIMAL_VALUE_0_STRING;
import static com.ecaservice.classifier.options.model.Constraints.DECIMAL_VALUE_1_STRING;
import static com.ecaservice.classifier.options.model.Constraints.HIDDEN_LAYER_REGEX;
import static com.ecaservice.classifier.options.model.Constraints.MAX_LENGTH_255;
import static com.ecaservice.classifier.options.model.Constraints.VALUE_1;
import static com.ecaservice.classifier.options.model.Constraints.VALUE_2;

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
    @Min(VALUE_1)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Neurons number in input layer")
    private Integer numInNeurons;

    /**
     * Neurons number in output layer
     */
    @Min(VALUE_2)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Neurons number in output layer")
    private Integer numOutNeurons;

    /**
     * Hidden layer structure
     */
    @Size(max = MAX_LENGTH_255)
    @Pattern(regexp = HIDDEN_LAYER_REGEX)
    @Schema(description = "Hidden layer structure")
    private String hiddenLayer;

    /**
     * Seed value for random generator
     */
    @Min(Integer.MIN_VALUE)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Seed value for random generator")
    private Integer seed;

    /**
     * Max. its for learning
     */
    @Min(VALUE_1)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Max. its for learning")
    private Integer numIterations;

    /**
     * Min. error for optimization algorithm
     */
    @DecimalMin(DECIMAL_VALUE_0_STRING)
    @DecimalMax(DECIMAL_VALUE_1_STRING)
    @Schema(description = "Min. error for optimization algorithm")
    private Double minError;

    /**
     * Activation function options
     */
    @Valid
    @Schema(description = "Activation function options")
    private ActivationFunctionOptions activationFunctionOptions;

    /**
     * Back propagation options
     */
    @Valid
    @Schema(description = "Back propagation options")
    private BackPropagationOptions backPropagationOptions;

}
