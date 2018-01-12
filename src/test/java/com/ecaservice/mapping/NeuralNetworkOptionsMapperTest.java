package com.ecaservice.mapping;

import com.ecaservice.model.options.ActivationFunctionOptions;
import com.ecaservice.model.options.NeuralNetworkOptions;
import eca.neural.NeuralNetwork;
import eca.neural.functions.AbstractFunction;
import eca.neural.functions.ActivationFunctionBuilder;
import eca.neural.functions.ActivationFunctionType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link NeuralNetworkOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(NeuralNetworkOptionsMapperImpl.class)
public class NeuralNetworkOptionsMapperTest {

    @Autowired
    private NeuralNetworkOptionsMapper neuralNetworkOptionsMapper;

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
        neuralNetworkOptions.setMaximumFractionDigits(4);
        NeuralNetwork neuralNetwork = neuralNetworkOptionsMapper.map(neuralNetworkOptions);
        assertThat(neuralNetwork.network().getActivationFunction()).isInstanceOf(AbstractFunction.class);
        AbstractFunction activationFunction = (AbstractFunction) neuralNetwork.network().getActivationFunction();
        assertThat(activationFunction.getCoefficient()).isEqualTo(
                neuralNetworkOptions.getActivationFunctionOptions().getCoefficient());
        assertThat(neuralNetwork.getDecimalFormat().getMaximumFractionDigits()).isEqualTo(
                neuralNetworkOptions.getMaximumFractionDigits());
    }
}
