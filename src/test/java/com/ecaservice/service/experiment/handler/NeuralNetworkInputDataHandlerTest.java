package com.ecaservice.service.experiment.handler;

import eca.neural.NeuralNetwork;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import weka.core.Instances;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link NeuralNetworkInputDataHandler} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(NeuralNetworkInputDataHandler.class)
public class NeuralNetworkInputDataHandlerTest {

    private static final int DEFAULT_NUM_CLASSES = 5;
    private static final int DEFAULT_NUM_ATTRIBUTES = 10;
    private static final int DEFAULT_NUM_INSTANCES = 100;

    @Autowired
    private NeuralNetworkInputDataHandler neuralNetworkInputDataHandler;

    @Mock
    private Instances data;

    /**
     * Test checking successful handling.
     */
    @Test
    public void testNeuralNetworkInputDataHandle() {
        NeuralNetwork neuralNetwork = new NeuralNetwork();
        when(data.numAttributes()).thenReturn(DEFAULT_NUM_ATTRIBUTES);
        when(data.numClasses()).thenReturn(DEFAULT_NUM_CLASSES);
        when(data.numInstances()).thenReturn(DEFAULT_NUM_INSTANCES);
        neuralNetworkInputDataHandler.handle(data, neuralNetwork);
        assertThat(neuralNetwork.network().inLayerNeuronsNum()).isEqualTo(DEFAULT_NUM_ATTRIBUTES - 1);
        assertThat(neuralNetwork.network().outLayerNeuronsNum()).isEqualTo(DEFAULT_NUM_CLASSES);
        assertThat(neuralNetwork.network().getHiddenLayer()).isNotNull();
    }
}
