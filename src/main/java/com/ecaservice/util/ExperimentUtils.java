package com.ecaservice.util;

import eca.metrics.KNearestNeighbours;
import eca.metrics.distances.ChebyshevDistance;
import eca.metrics.distances.Distance;
import eca.metrics.distances.EuclidDistance;
import eca.metrics.distances.ManhattanDistance;
import eca.metrics.distances.SquareEuclidDistance;
import eca.neural.NeuralNetwork;
import eca.neural.functions.ActivationFunction;
import eca.neural.functions.ExponentialFunction;
import eca.neural.functions.HyperbolicTangentFunction;
import eca.neural.functions.LogisticFunction;
import eca.neural.functions.SinusoidFunction;
import eca.neural.functions.SoftSignFunction;
import eca.regression.Logistic;
import eca.trees.C45;
import eca.trees.CART;
import eca.trees.CHAID;
import eca.trees.ID3;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

/**
 * Experiment utility class.
 *
 * @author Roman Batygin
 */

public class ExperimentUtils {

    /**
     * Build individual classifiers set.
     *
     * @param data                  {@link Instances} object
     * @param maximumFractionDigits maximum fraction digits
     * @return individual classifiers list
     */
    public static List<AbstractClassifier> builtClassifiersSet(Instances data, int maximumFractionDigits) {
        List<AbstractClassifier> classifiers = new ArrayList<>();
        classifiers.add(new Logistic());
        classifiers.add(new CART());
        classifiers.add(new C45());
        classifiers.add(new CHAID());
        classifiers.add(new ID3());
        classifiers.add(createKNearestNeighbours(new EuclidDistance(), maximumFractionDigits));
        classifiers.add(createKNearestNeighbours(new SquareEuclidDistance(), maximumFractionDigits));
        classifiers.add(createKNearestNeighbours(new ManhattanDistance(), maximumFractionDigits));
        classifiers.add(createKNearestNeighbours(new ChebyshevDistance(), maximumFractionDigits));
        classifiers.add(createNeuralNetwork(data, new LogisticFunction(), maximumFractionDigits));
        classifiers.add(createNeuralNetwork(data, new HyperbolicTangentFunction(), maximumFractionDigits));
        classifiers.add(createNeuralNetwork(data, new SinusoidFunction(), maximumFractionDigits));
        classifiers.add(createNeuralNetwork(data, new SoftSignFunction(), maximumFractionDigits));
        classifiers.add(createNeuralNetwork(data, new ExponentialFunction(), maximumFractionDigits));
        return classifiers;
    }

    private static KNearestNeighbours createKNearestNeighbours(Distance distance, int maximumFractionDigits) {
        KNearestNeighbours kNearestNeighbours = new KNearestNeighbours(distance);
        kNearestNeighbours.getDecimalFormat().setMaximumFractionDigits(maximumFractionDigits);
        return kNearestNeighbours;
    }

    private static NeuralNetwork createNeuralNetwork(Instances data, ActivationFunction activationFunction,
                                                     int maximumFractionDigits) {
        NeuralNetwork neuralNetwork = new NeuralNetwork(data);
        neuralNetwork.network().setActivationFunction(activationFunction);
        neuralNetwork.getDecimalFormat().setMaximumFractionDigits(maximumFractionDigits);
        return neuralNetwork;
    }
}
