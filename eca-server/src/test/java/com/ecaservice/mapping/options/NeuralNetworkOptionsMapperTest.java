package com.ecaservice.mapping.options;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.options.ActivationFunctionOptions;
import com.ecaservice.model.options.NeuralNetworkOptions;
import eca.neural.BackPropagation;
import eca.neural.NeuralNetwork;
import eca.neural.functions.AbstractFunction;
import eca.neural.functions.ActivationFunctionBuilder;
import eca.neural.functions.ActivationFunctionType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link NeuralNetworkOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class NeuralNetworkOptionsMapperTest {

    private static final double COEFFICIENT = 2.0d;

    @Inject
    private NeuralNetworkOptionsMapper neuralNetworkOptionsMapper;
    @Inject
    private ExperimentConfig experimentConfig;

    @Test
    public void testMapNeuralNetworkOptions() {
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
        assertThat(neuralNetwork.getDecimalFormat().getMaximumFractionDigits()).isEqualTo(
                experimentConfig.getMaximumFractionDigits());
        assertThat(neuralNetwork.getMultilayerPerceptron().getNumInNeurons()).isEqualTo(
                neuralNetworkOptions.getNumInNeurons());
        assertThat(neuralNetwork.getMultilayerPerceptron().getNumOutNeurons()).isEqualTo(
                neuralNetworkOptions.getNumOutNeurons());
        assertThat(neuralNetwork.getMultilayerPerceptron().getNumIterations()).isEqualTo(
                neuralNetworkOptions.getNumIterations());
        assertThat(neuralNetwork.getMultilayerPerceptron().getMinError()).isEqualTo(
                neuralNetworkOptions.getMinError());
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
