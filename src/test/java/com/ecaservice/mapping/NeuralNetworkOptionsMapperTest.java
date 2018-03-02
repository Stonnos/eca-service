package com.ecaservice.mapping;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.options.ActivationFunctionOptions;
import com.ecaservice.model.options.NeuralNetworkOptions;
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

    @Inject
    private NeuralNetworkOptionsMapper neuralNetworkOptionsMapper;
    @Inject
    private ExperimentConfig experimentConfig;

    @Test
    public void testMapNeuralNetworkOptions() {
        NeuralNetworkOptions neuralNetworkOptions = new NeuralNetworkOptions();
        neuralNetworkOptions.setActivationFunctionOptions(new ActivationFunctionOptions());
        ActivationFunctionBuilder activationFunctionBuilder = new ActivationFunctionBuilder();
        for (ActivationFunctionType activationFunctionType : ActivationFunctionType.values()) {
            neuralNetworkOptions.getActivationFunctionOptions().setActivationFunctionType(activationFunctionType);
            assertThat(neuralNetworkOptionsMapper.map(
                    neuralNetworkOptions).network().getActivationFunction()).isInstanceOf(
                    activationFunctionType.handle(activationFunctionBuilder).getClass());
        }
        neuralNetworkOptions.getActivationFunctionOptions().setCoefficient(2.0D);
        NeuralNetwork neuralNetwork = neuralNetworkOptionsMapper.map(neuralNetworkOptions);
        assertThat(neuralNetwork.network().getActivationFunction()).isInstanceOf(AbstractFunction.class);
        AbstractFunction activationFunction = (AbstractFunction) neuralNetwork.network().getActivationFunction();
        assertThat(activationFunction.getCoefficient()).isEqualTo(
                neuralNetworkOptions.getActivationFunctionOptions().getCoefficient());
        assertThat(neuralNetwork.getDecimalFormat().getMaximumFractionDigits()).isEqualTo(
                experimentConfig.getMaximumFractionDigits());
    }
}
