package com.ecaservice.mapping;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.options.ActivationFunctionOptions;
import com.ecaservice.model.options.NeuralNetworkOptions;
import eca.neural.NeuralNetwork;
import eca.neural.functions.AbstractFunction;
import eca.neural.functions.ActivationFunctionBuilder;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import javax.inject.Inject;

/**
 * Implements neural network input options mapping to neural network model.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class NeuralNetworkOptionsMapper extends ClassifierOptionsMapper<NeuralNetworkOptions, NeuralNetwork> {

    @Inject
    private ExperimentConfig experimentConfig;

    protected NeuralNetworkOptionsMapper() {
        super(NeuralNetworkOptions.class);
    }

    @AfterMapping
    protected void mapActivationFunction(NeuralNetworkOptions networkOptions,
                                         @MappingTarget NeuralNetwork neuralNetwork) {
        ActivationFunctionOptions activationFunctionOptions = networkOptions.getActivationFunctionOptions();
        if (activationFunctionOptions != null) {
            AbstractFunction activationFunction =
                    activationFunctionOptions.getActivationFunctionType().handle(new ActivationFunctionBuilder());
            if (activationFunctionOptions.getCoefficient() != null) {
                activationFunction.setCoefficient(activationFunctionOptions.getCoefficient());
            }
            neuralNetwork.network().setActivationFunction(activationFunction);
        }
    }

    @AfterMapping
    protected void mapMaxFractionDigits(NeuralNetworkOptions networkOptions,
                                        @MappingTarget NeuralNetwork neuralNetwork) {
        if (experimentConfig.getMaximumFractionDigits() != null) {
            neuralNetwork.getDecimalFormat().setMaximumFractionDigits(experimentConfig.getMaximumFractionDigits());
        }
    }

}
