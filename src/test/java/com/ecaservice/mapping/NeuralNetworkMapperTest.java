package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.options.NeuralNetworkOptions;
import eca.neural.BackPropagation;
import eca.neural.NeuralNetwork;
import eca.neural.functions.LogisticFunction;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link NeuralNetworkMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class NeuralNetworkMapperTest {

    private static final double COEFFICIENT = 2d;

    @Inject
    private NeuralNetworkMapper neuralNetworkMapper;

    @Test
    public void testMapNeuralNetwork() {
        LogisticFunction logisticFunction = new LogisticFunction(COEFFICIENT);
        NeuralNetwork neuralNetwork = TestHelperUtils.createNeuralNetwork(logisticFunction);
        NeuralNetworkOptions options = neuralNetworkMapper.map(neuralNetwork);
        Assertions.assertThat(options.getNumInNeurons()).isEqualTo(
                neuralNetwork.getMultilayerPerceptron().getNumInNeurons());
        Assertions.assertThat(options.getNumOutNeurons()).isEqualTo(
                neuralNetwork.getMultilayerPerceptron().getNumOutNeurons());
        Assertions.assertThat(options.getHiddenLayer()).isEqualTo(
                neuralNetwork.getMultilayerPerceptron().getHiddenLayer());
        Assertions.assertThat(options.getSeed()).isEqualTo(neuralNetwork.getSeed());
        Assertions.assertThat(options.getMinError()).isEqualTo(neuralNetwork.getMultilayerPerceptron().getMinError());
        Assertions.assertThat(options.getNumIterations()).isEqualTo(
                neuralNetwork.getMultilayerPerceptron().getNumIterations());
        Assertions.assertThat(options.getActivationFunctionOptions()).isNotNull();
        Assertions.assertThat(options.getActivationFunctionOptions().getActivationFunctionType()).isEqualTo(
                neuralNetwork.getMultilayerPerceptron().getActivationFunction().getActivationFunctionType());
        Assertions.assertThat(options.getActivationFunctionOptions().getCoefficient()).isEqualTo(
                logisticFunction.getCoefficient());
        Assertions.assertThat(options.getBackPropagationOptions()).isNotNull();
        BackPropagation backPropagation =
                (BackPropagation) neuralNetwork.getMultilayerPerceptron().getLearningAlgorithm();
        Assertions.assertThat(options.getBackPropagationOptions().getLearningRate()).isEqualTo(
                backPropagation.getLearningRate());
        Assertions.assertThat(options.getBackPropagationOptions().getMomentum()).isEqualTo(
                backPropagation.getMomentum());
    }
}
