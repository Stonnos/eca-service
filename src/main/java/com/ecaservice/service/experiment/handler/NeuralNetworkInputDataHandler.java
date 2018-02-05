package com.ecaservice.service.experiment.handler;

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
        classifier.network().setInLayerNeuronsNum(data.numAttributes() - 1);
        classifier.network().setOutLayerNeuronsNum(data.numClasses());
        classifier.network().setHiddenLayer(String.valueOf(NeuralNetworkUtil.getMinNumNeuronsInHiddenLayer(data)));
    }
}
