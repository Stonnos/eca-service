package com.ecaservice.server.service.experiment.handler;

import eca.neural.NeuralNetwork;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import weka.core.Instances;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link NeuralNetworkInputDataHandler} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(NeuralNetworkInputDataHandler.class)
class NeuralNetworkInputDataHandlerTest {

    private static final int DEFAULT_NUM_CLASSES = 5;
    private static final int DEFAULT_NUM_ATTRIBUTES = 10;
    private static final int DEFAULT_NUM_INSTANCES = 100;

    @Inject
    private NeuralNetworkInputDataHandler neuralNetworkInputDataHandler;

    @Mock
    private Instances data;

    /**
     * Test checking successful handling.
     */
    @Test
    void testNeuralNetworkInputDataHandle() {
        NeuralNetwork neuralNetwork = new NeuralNetwork();
        when(data.numAttributes()).thenReturn(DEFAULT_NUM_ATTRIBUTES);
        when(data.numClasses()).thenReturn(DEFAULT_NUM_CLASSES);
        when(data.numInstances()).thenReturn(DEFAULT_NUM_INSTANCES);
        neuralNetworkInputDataHandler.handle(data, neuralNetwork);
        assertThat(neuralNetwork.getMultilayerPerceptron().getNumInNeurons()).isEqualTo(DEFAULT_NUM_ATTRIBUTES - 1);
        assertThat(neuralNetwork.getMultilayerPerceptron().getNumOutNeurons()).isEqualTo(DEFAULT_NUM_CLASSES);
        assertThat(neuralNetwork.getMultilayerPerceptron().getHiddenLayer()).isNotNull();
    }
}
