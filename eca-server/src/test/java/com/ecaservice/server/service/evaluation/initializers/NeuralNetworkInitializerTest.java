package com.ecaservice.server.service.evaluation.initializers;

import eca.neural.NeuralNetwork;
import eca.neural.NeuralNetworkUtil;
import org.junit.jupiter.api.BeforeEach;
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
 * Unit tests for checking {@link NeuralNetworkInitializer} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(NeuralNetworkInitializer.class)
class NeuralNetworkInitializerTest {

    private static final int DEFAULT_NUM_CLASSES = 5;
    private static final int DEFAULT_NUM_ATTRIBUTES = 10;
    private static final int DEFAULT_NUM_INSTANCES = 100;
    private static final String HIDDEN_LAYER = "5,4,7";

    @Inject
    private NeuralNetworkInitializer neuralNetworkInitializer;

    @Mock
    private Instances data;

    @BeforeEach
    void init() {
        when(data.numAttributes()).thenReturn(DEFAULT_NUM_ATTRIBUTES);
        when(data.numClasses()).thenReturn(DEFAULT_NUM_CLASSES);
        when(data.numInstances()).thenReturn(DEFAULT_NUM_INSTANCES);
    }

    @Test
    void testInitializeNeuralNetworkWithEmptyHiddenLayer() {
        NeuralNetwork neuralNetwork = new NeuralNetwork();
        neuralNetworkInitializer.handle(data, neuralNetwork);
        assertThat(neuralNetwork.getMultilayerPerceptron().getNumInNeurons()).isEqualTo(DEFAULT_NUM_ATTRIBUTES - 1);
        assertThat(neuralNetwork.getMultilayerPerceptron().getNumOutNeurons()).isEqualTo(DEFAULT_NUM_CLASSES);
        assertThat(neuralNetwork.getMultilayerPerceptron().getHiddenLayer()).isEqualTo(
                String.valueOf(NeuralNetworkUtil.getMinNumNeuronsInHiddenLayer(data)));
    }

    @Test
    void testInitializeNeuralNetworkWithNotEmptyHiddenLayer() {
        NeuralNetwork neuralNetwork = new NeuralNetwork();
        neuralNetwork.getMultilayerPerceptron().setHiddenLayer(HIDDEN_LAYER);
        neuralNetworkInitializer.handle(data, neuralNetwork);
        assertThat(neuralNetwork.getMultilayerPerceptron().getHiddenLayer()).isEqualTo(HIDDEN_LAYER);
    }
}
