package com.ecaservice.server.service.evaluation.initializers;

import eca.neural.NeuralNetwork;
import eca.neural.NeuralNetworkUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import weka.core.Instances;

/**
 * Implements neural network input options initialization.
 *
 * @author Roman Batygin
 */
@Component
public class NeuralNetworkInitializer extends ClassifierInitializer<NeuralNetwork> {

    protected NeuralNetworkInitializer() {
        super(NeuralNetwork.class);
    }

    @Override
    protected void internalHandle(Instances data, NeuralNetwork classifier) {
        classifier.getMultilayerPerceptron().setNumInNeurons(data.numAttributes() - 1);
        classifier.getMultilayerPerceptron().setNumOutNeurons(data.numClasses());
        if (StringUtils.isEmpty(classifier.getMultilayerPerceptron().getHiddenLayer())) {
            classifier.getMultilayerPerceptron().setHiddenLayer(
                    String.valueOf(NeuralNetworkUtil.getMinNumNeuronsInHiddenLayer(data)));
        }
    }
}
