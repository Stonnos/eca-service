package com.ecaservice.server.service.experiment.handler;

import eca.neural.NeuralNetwork;
import eca.neural.NeuralNetworkUtil;
import org.springframework.stereotype.Component;
import weka.core.Instances;

/**
 * Implements neural network input options initialization.
 *
 * @author Roman Batygin
 */
@Component
public class NeuralNetworkInputDataHandler extends ClassifierInputDataHandler<NeuralNetwork> {

    protected NeuralNetworkInputDataHandler() {
        super(NeuralNetwork.class);
    }

    @Override
    protected void internalHandle(Instances data, NeuralNetwork classifier) {
        classifier.getMultilayerPerceptron().setNumInNeurons(data.numAttributes() - 1);
        classifier.getMultilayerPerceptron().setNumOutNeurons(data.numClasses());
        classifier.getMultilayerPerceptron().setHiddenLayer(
                String.valueOf(NeuralNetworkUtil.getMinNumNeuronsInHiddenLayer(data)));
    }
}
