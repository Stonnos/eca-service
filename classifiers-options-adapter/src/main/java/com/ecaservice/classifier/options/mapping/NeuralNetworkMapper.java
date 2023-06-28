package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.ActivationFunctionOptions;
import com.ecaservice.classifier.options.model.BackPropagationOptions;
import com.ecaservice.classifier.options.model.NeuralNetworkOptions;
import eca.neural.BackPropagation;
import eca.neural.NeuralNetwork;
import eca.neural.functions.AbstractFunction;
import eca.neural.functions.ActivationFunction;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.core.annotation.Order;

/**
 * Implements mapping Neural network classifier to its options model.
 *
 * @author Roman Batygin
 */
@Order(Ordered.NEURAL_NETWORK_ORDER)
@Mapper
public abstract class NeuralNetworkMapper extends AbstractClassifierMapper<NeuralNetwork, NeuralNetworkOptions> {

    protected NeuralNetworkMapper() {
        super(NeuralNetwork.class);
    }

    /**
     * Maps neural network to its options model.
     *
     * @param neuralNetwork - neural network
     * @return neural network options
     */
    @Mapping(source = "multilayerPerceptron.numInNeurons", target = "numInNeurons")
    @Mapping(source = "multilayerPerceptron.numOutNeurons", target = "numOutNeurons")
    @Mapping(source = "multilayerPerceptron.minError", target = "minError")
    @Mapping(source = "multilayerPerceptron.numIterations", target = "numIterations")
    @Mapping(source = "multilayerPerceptron.hiddenLayer", target = "hiddenLayer")
    public abstract NeuralNetworkOptions map(NeuralNetwork neuralNetwork);

    @AfterMapping
    protected void mapActivationFunction(NeuralNetwork neuralNetwork, @MappingTarget NeuralNetworkOptions options) {
        ActivationFunctionOptions activationFunctionOptions = new ActivationFunctionOptions();
        ActivationFunction activationFunction = neuralNetwork.getMultilayerPerceptron().getActivationFunction();
        activationFunctionOptions.setActivationFunctionType(activationFunction.getActivationFunctionType());
        if (activationFunction instanceof AbstractFunction) {
            activationFunctionOptions.setCoefficient(((AbstractFunction) activationFunction).getCoefficient());
        }
        options.setActivationFunctionOptions(activationFunctionOptions);
    }

    @AfterMapping
    protected void mapLearningAlgorithm(NeuralNetwork neuralNetwork, @MappingTarget NeuralNetworkOptions options) {
        if (neuralNetwork.getMultilayerPerceptron().getLearningAlgorithm() instanceof BackPropagation) {
            BackPropagation backPropagation =
                    (BackPropagation) neuralNetwork.getMultilayerPerceptron().getLearningAlgorithm();
            BackPropagationOptions backPropagationOptions = new BackPropagationOptions();
            backPropagationOptions.setLearningRate(backPropagation.getLearningRate());
            backPropagationOptions.setMomentum(backPropagation.getMomentum());
            options.setBackPropagationOptions(backPropagationOptions);
        }
    }
}
