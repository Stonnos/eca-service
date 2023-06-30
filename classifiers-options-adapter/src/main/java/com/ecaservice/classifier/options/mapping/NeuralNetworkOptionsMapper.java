package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.ActivationFunctionOptions;
import com.ecaservice.classifier.options.model.BackPropagationOptions;
import com.ecaservice.classifier.options.model.NeuralNetworkOptions;
import eca.neural.BackPropagation;
import eca.neural.NeuralNetwork;
import eca.neural.functions.AbstractFunction;
import eca.neural.functions.ActivationFunctionBuilder;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.core.annotation.Order;

/**
 * Implements neural network input options mapping to neural network model.
 *
 * @author Roman Batygin
 */
@Order(Ordered.NEURAL_NETWORK_ORDER)
@Mapper
public abstract class NeuralNetworkOptionsMapper extends ClassifierOptionsMapper<NeuralNetworkOptions, NeuralNetwork> {

    private static final ActivationFunctionBuilder ACTIVATION_FUNCTION_BUILDER = new ActivationFunctionBuilder();

    protected NeuralNetworkOptionsMapper() {
        super(NeuralNetworkOptions.class);
    }

    @AfterMapping
    protected void mapActivationFunction(NeuralNetworkOptions networkOptions,
                                         @MappingTarget NeuralNetwork neuralNetwork) {
        ActivationFunctionOptions activationFunctionOptions = networkOptions.getActivationFunctionOptions();
        if (activationFunctionOptions != null) {
            AbstractFunction activationFunction =
                    activationFunctionOptions.getActivationFunctionType().handle(ACTIVATION_FUNCTION_BUILDER);
            if (activationFunctionOptions.getCoefficient() != null) {
                activationFunction.setCoefficient(activationFunctionOptions.getCoefficient());
            }
            neuralNetwork.getMultilayerPerceptron().setActivationFunction(activationFunction);
        }
    }

    @AfterMapping
    protected void mapLearningAlgorithm(NeuralNetworkOptions networkOptions,
                                        @MappingTarget NeuralNetwork neuralNetwork) {
        BackPropagationOptions backPropagationOptions = networkOptions.getBackPropagationOptions();
        if (backPropagationOptions != null) {
            BackPropagation backPropagation = new BackPropagation(neuralNetwork.getMultilayerPerceptron());
            if (backPropagationOptions.getLearningRate() != null) {
                backPropagation.setLearningRate(backPropagationOptions.getLearningRate());
            }
            if (backPropagationOptions.getMomentum() != null) {
                backPropagation.setMomentum(backPropagationOptions.getMomentum());
            }
            neuralNetwork.getMultilayerPerceptron().setLearningAlgorithm(backPropagation);
        }
    }

    @AfterMapping
    protected void postMapping(NeuralNetworkOptions networkOptions,
                               @MappingTarget NeuralNetwork neuralNetwork) {
        if (networkOptions.getNumInNeurons() != null) {
            neuralNetwork.getMultilayerPerceptron().setNumInNeurons(networkOptions.getNumInNeurons());
        }
        if (networkOptions.getNumOutNeurons() != null) {
            neuralNetwork.getMultilayerPerceptron().setNumOutNeurons(networkOptions.getNumOutNeurons());
        }
        if (networkOptions.getMinError() != null) {
            neuralNetwork.getMultilayerPerceptron().setMinError(networkOptions.getMinError());
        }
        if (networkOptions.getNumIterations() != null) {
            neuralNetwork.getMultilayerPerceptron().setNumIterations(networkOptions.getNumIterations());
        }
        if (!StringUtils.isEmpty(networkOptions.getHiddenLayer())) {
            neuralNetwork.getMultilayerPerceptron().setHiddenLayer(networkOptions.getHiddenLayer());
        }
    }

}
