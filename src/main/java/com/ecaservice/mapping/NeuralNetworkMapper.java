package com.ecaservice.mapping;

import com.ecaservice.model.options.ActivationFunctionOptions;
import com.ecaservice.model.options.BackPropagationOptions;
import com.ecaservice.model.options.NeuralNetworkOptions;
import eca.neural.BackPropagation;
import eca.neural.NeuralNetwork;
import eca.neural.functions.AbstractFunction;
import eca.neural.functions.ActivationFunction;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Implements mapping Neural network classifier to its options model.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class NeuralNetworkMapper extends AbstractClassifierMapper<NeuralNetwork, NeuralNetworkOptions> {

    protected NeuralNetworkMapper() {
        super(NeuralNetwork.class);
    }

    @AfterMapping
    protected void mapActivationFunction(NeuralNetwork neuralNetwork, @MappingTarget NeuralNetworkOptions options) {
        ActivationFunctionOptions activationFunctionOptions = new ActivationFunctionOptions();
        ActivationFunction activationFunction = neuralNetwork.network().getActivationFunction();
        activationFunctionOptions.setActivationFunctionType(activationFunction.getActivationFunctionType());
        if (activationFunction instanceof AbstractFunction) {
            activationFunctionOptions.setCoefficient(((AbstractFunction) activationFunction).getCoefficient());
        }
        options.setActivationFunctionOptions(activationFunctionOptions);
    }

    @AfterMapping
    protected void postMapping(NeuralNetwork neuralNetwork, @MappingTarget NeuralNetworkOptions options) {
        options.setNumInNeurons(neuralNetwork.network().inLayerNeuronsNum());
        options.setNumOutNeurons(neuralNetwork.network().outLayerNeuronsNum());
        options.setNumIterations(neuralNetwork.network().getMaxIterationsNum());
        options.setMinError(neuralNetwork.network().getMinError());
        options.setHiddenLayer(neuralNetwork.network().getHiddenLayer());
        if (neuralNetwork.network().getLearningAlgorithm() instanceof BackPropagation) {
            BackPropagation backPropagation = (BackPropagation) neuralNetwork.network().getLearningAlgorithm();
            BackPropagationOptions backPropagationOptions = new BackPropagationOptions();
            backPropagationOptions.setLearningRate(backPropagation.getLearningRate());
            backPropagationOptions.setMomentum(backPropagation.getMomentum());
            options.setBackPropagationOptions(backPropagationOptions);
        }
    }
}
