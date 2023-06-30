package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.TestHelperUtils;
import com.ecaservice.classifier.options.model.ActivationFunctionOptions;
import com.ecaservice.classifier.options.model.NeuralNetworkOptions;
import eca.neural.BackPropagation;
import eca.neural.NeuralNetwork;
import eca.neural.functions.AbstractFunction;
import eca.neural.functions.ActivationFunctionBuilder;
import eca.neural.functions.ActivationFunctionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link NeuralNetworkOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(NeuralNetworkOptionsMapperImpl.class)
class NeuralNetworkOptionsMapperTest {

    private static final double COEFFICIENT = 2.0d;

    @Inject
    private NeuralNetworkOptionsMapper neuralNetworkOptionsMapper;

    @Test
    void testMapNeuralNetworkOptions() {
        NeuralNetworkOptions neuralNetworkOptions = TestHelperUtils.createNeuralNetworkOptions();
        neuralNetworkOptions.setActivationFunctionOptions(new ActivationFunctionOptions());
        ActivationFunctionBuilder activationFunctionBuilder = new ActivationFunctionBuilder();
        for (ActivationFunctionType activationFunctionType : ActivationFunctionType.values()) {
            neuralNetworkOptions.getActivationFunctionOptions().setActivationFunctionType(activationFunctionType);
            assertThat(neuralNetworkOptionsMapper.map(
                    neuralNetworkOptions).getMultilayerPerceptron().getActivationFunction()).isInstanceOf(
                    activationFunctionType.handle(activationFunctionBuilder).getClass());
        }
        neuralNetworkOptions.getActivationFunctionOptions().setCoefficient(COEFFICIENT);
        NeuralNetwork neuralNetwork = neuralNetworkOptionsMapper.map(neuralNetworkOptions);
        assertThat(neuralNetwork.getMultilayerPerceptron().getActivationFunction()).isInstanceOf(
                AbstractFunction.class);
        AbstractFunction activationFunction =
                (AbstractFunction) neuralNetwork.getMultilayerPerceptron().getActivationFunction();
        assertThat(activationFunction.getCoefficient()).isEqualTo(
                neuralNetworkOptions.getActivationFunctionOptions().getCoefficient());
        assertThat(neuralNetwork.getMultilayerPerceptron().getNumInNeurons()).isEqualTo(
                neuralNetworkOptions.getNumInNeurons());
        assertThat(neuralNetwork.getMultilayerPerceptron().getNumOutNeurons()).isEqualTo(
                neuralNetworkOptions.getNumOutNeurons());
        assertThat(neuralNetwork.getMultilayerPerceptron().getNumIterations()).isEqualTo(
                neuralNetworkOptions.getNumIterations());
        assertThat(neuralNetwork.getMultilayerPerceptron().getMinError()).isEqualTo(
                neuralNetworkOptions.getMinError().doubleValue());
        assertThat(neuralNetwork.getMultilayerPerceptron().getHiddenLayer()).isEqualTo(
                neuralNetworkOptions.getHiddenLayer());
        assertThat(neuralNetwork.getMultilayerPerceptron().getLearningAlgorithm()).isInstanceOf(BackPropagation.class);
        BackPropagation backPropagation =
                (BackPropagation) neuralNetwork.getMultilayerPerceptron().getLearningAlgorithm();
        assertThat(backPropagation.getLearningRate()).isEqualTo(
                neuralNetworkOptions.getBackPropagationOptions().getLearningRate());
        assertThat(backPropagation.getMomentum()).isEqualTo(
                neuralNetworkOptions.getBackPropagationOptions().getMomentum());
    }
}
