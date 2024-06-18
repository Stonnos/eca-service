package com.ecaservice.classifier.options;

import com.ecaservice.classifier.options.model.AdaBoostOptions;
import com.ecaservice.classifier.options.model.BackPropagationOptions;
import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.classifier.options.model.DecisionTreeOptions;
import com.ecaservice.classifier.options.model.ExtraTreesOptions;
import com.ecaservice.classifier.options.model.HeterogeneousClassifierOptions;
import com.ecaservice.classifier.options.model.J48Options;
import com.ecaservice.classifier.options.model.KNearestNeighboursOptions;
import com.ecaservice.classifier.options.model.NeuralNetworkOptions;
import com.ecaservice.classifier.options.model.RandomForestsOptions;
import com.ecaservice.classifier.options.model.RandomNetworkOptions;
import com.ecaservice.classifier.options.model.StackingOptions;
import eca.ensemble.RandomNetworks;
import eca.ensemble.StackingClassifier;
import eca.ensemble.forests.DecisionTreeBuilder;
import eca.ensemble.forests.DecisionTreeType;
import eca.ensemble.forests.ExtraTreesClassifier;
import eca.ensemble.forests.RandomForests;
import eca.ensemble.sampling.SamplingMethod;
import eca.metrics.KNearestNeighbours;
import eca.metrics.distances.Distance;
import eca.neural.NeuralNetwork;
import eca.neural.functions.AbstractFunction;
import eca.trees.DecisionTreeClassifier;
import eca.trees.J48;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Test helper utility class.
 * @author Roman Batygin
 */
@UtilityClass
public class TestHelperUtils {

    private static final int NUM_FOLDS = 3;
    private static final int SEED = 1;
    private static final int NUM_OBJ = 2;
    private static final double KNN_WEIGHT = 0.55d;
    private static final int NUM_NEIGHBOURS = 25;
    private static final int NUM_RANDOM_ATTR = 10;
    private static final int NUM_RANDOM_SPLITS = 25;
    private static final String HIDDEN_LAYER = "5,8,9";
    private static final int IN_LAYER_NEURONS_NUM = 12;
    private static final int OUT_LAYER_NEURONS_NUM = 7;
    private static final int MAX_DEPTH = 10;
    private static final int NUM_ITERATIONS = 5;
    private static final int NUM_THREADS = 2;
    private static final double MIN_ERROR = 0.00001d;
    private static final int NUM_IN_NEURONS = 10;
    private static final int NUM_OUT_NEURONS = 12;
    private static final double LEARNING_RATE = 0.01d;
    private static final double MOMENTUM = 0.23d;
    private static final double MIN_ERROR_THRESHOLD = 0.0d;
    private static final double MAX_ERROR_THRESHOLD = 0.5d;

    /**
     * Creates J48 classifier.
     *
     * @return J48 classifier
     */
    public static J48 createJ48() {
        J48 j48 = new J48();
        j48.setUnpruned(true);
        j48.setNumFolds(NUM_FOLDS);
        j48.setMinNumObj(NUM_OBJ);
        j48.setBinarySplits(true);
        return j48;
    }

    /**
     * Creates KNN with specified distance function.
     *
     * @param distance - distance function
     * @return KNN object
     */
    public static KNearestNeighbours createKNearestNeighbours(Distance distance) {
        KNearestNeighbours kNearestNeighbours = new KNearestNeighbours();
        kNearestNeighbours.setDistance(distance);
        kNearestNeighbours.setWeight(KNN_WEIGHT);
        kNearestNeighbours.setNumNeighbours(NUM_NEIGHBOURS);
        return kNearestNeighbours;
    }

    /**
     * Creates neural network with specified activation function.
     *
     * @param abstractFunction - activation function
     * @return neural network object
     */
    public static NeuralNetwork createNeuralNetwork(AbstractFunction abstractFunction) {
        NeuralNetwork neuralNetwork = new NeuralNetwork();
        neuralNetwork.setSeed(SEED);
        neuralNetwork.getMultilayerPerceptron().setNumInNeurons(IN_LAYER_NEURONS_NUM);
        neuralNetwork.getMultilayerPerceptron().setNumOutNeurons(OUT_LAYER_NEURONS_NUM);
        neuralNetwork.getMultilayerPerceptron().setHiddenLayer(HIDDEN_LAYER);
        neuralNetwork.getMultilayerPerceptron().setActivationFunction(abstractFunction);
        return neuralNetwork;
    }

    /**
     * Creates decision tree.
     *
     * @param treeType - tree type.
     * @return decision tree object
     */
    public static DecisionTreeClassifier createDecisionTreeClassifier(DecisionTreeType treeType) {
        DecisionTreeClassifier treeClassifier = treeType.handle(new DecisionTreeBuilder());
        treeClassifier.setSeed(SEED);
        treeClassifier.setMinObj(NUM_OBJ);
        treeClassifier.setUseBinarySplits(true);
        treeClassifier.setRandomTree(true);
        treeClassifier.setNumRandomAttr(NUM_RANDOM_ATTR);
        treeClassifier.setNumRandomSplits(NUM_RANDOM_SPLITS);
        return treeClassifier;
    }

    /**
     * Creates random forests classifier.
     *
     * @param decisionTreeType - decision tree type
     * @return random forests classifier
     */
    public static RandomForests createRandomForests(DecisionTreeType decisionTreeType) {
        RandomForests randomForests = new RandomForests();
        randomForests.setSeed(SEED);
        randomForests.setNumThreads(NUM_THREADS);
        randomForests.setNumIterations(NUM_ITERATIONS);
        randomForests.setDecisionTreeType(decisionTreeType);
        randomForests.setMaxDepth(MAX_DEPTH);
        randomForests.setMinObj(NUM_OBJ);
        randomForests.setNumRandomAttr(NUM_RANDOM_ATTR);
        return randomForests;
    }

    /**
     * Creates extra trees classifier.
     *
     * @param decisionTreeType - decision tree type
     * @return extra trees classifier
     */
    public static ExtraTreesClassifier createExtraTreesClassifier(DecisionTreeType decisionTreeType) {
        ExtraTreesClassifier treesClassifier = new ExtraTreesClassifier();
        treesClassifier.setSeed(SEED);
        treesClassifier.setNumThreads(NUM_THREADS);
        treesClassifier.setNumIterations(NUM_ITERATIONS);
        treesClassifier.setDecisionTreeType(decisionTreeType);
        treesClassifier.setMaxDepth(MAX_DEPTH);
        treesClassifier.setMinObj(NUM_OBJ);
        treesClassifier.setNumRandomAttr(NUM_RANDOM_ATTR);
        treesClassifier.setUseBootstrapSamples(true);
        treesClassifier.setNumRandomSplits(NUM_RANDOM_SPLITS);
        return treesClassifier;
    }

    /**
     * Creates random networks object.
     *
     * @return random networks object
     */
    public static RandomNetworks createRandomNetworks() {
        RandomNetworks randomNetworks = new RandomNetworks();
        randomNetworks.setSeed(SEED);
        randomNetworks.setNumThreads(NUM_THREADS);
        randomNetworks.setUseBootstrapSamples(true);
        randomNetworks.setNumIterations(NUM_ITERATIONS);
        randomNetworks.setMinError(MIN_ERROR_THRESHOLD);
        randomNetworks.setMaxError(MAX_ERROR_THRESHOLD);
        return randomNetworks;
    }

    /**
     * Creates stacking classifier.
     *
     * @return stacking classifier
     */
    public static StackingClassifier createStackingClassifier() {
        StackingClassifier stackingClassifier = new StackingClassifier();
        stackingClassifier.setSeed(SEED);
        stackingClassifier.setUseCrossValidation(true);
        stackingClassifier.setNumFolds(NUM_FOLDS);
        return stackingClassifier;
    }

    /**
     * Creates decision tree options.
     *
     * @return decision tree options
     */
    public static DecisionTreeOptions createDecisionTreeOptions() {
        DecisionTreeOptions decisionTreeOptions = new DecisionTreeOptions();
        decisionTreeOptions.setMaxDepth(MAX_DEPTH);
        decisionTreeOptions.setMinObj(NUM_OBJ);
        decisionTreeOptions.setNumRandomAttr(NUM_RANDOM_ATTR);
        decisionTreeOptions.setNumRandomSplits(NUM_RANDOM_SPLITS);
        decisionTreeOptions.setRandomTree(true);
        decisionTreeOptions.setUseBinarySplits(true);
        decisionTreeOptions.setUseRandomSplits(false);
        decisionTreeOptions.setSeed(SEED);
        return decisionTreeOptions;
    }

    /**
     * Creates neural network options.
     *
     * @return neural network options
     */
    public static NeuralNetworkOptions createNeuralNetworkOptions() {
        NeuralNetworkOptions neuralNetworkOptions = new NeuralNetworkOptions();
        neuralNetworkOptions.setNumIterations(NUM_ITERATIONS);
        neuralNetworkOptions.setSeed(SEED);
        neuralNetworkOptions.setHiddenLayer(HIDDEN_LAYER);
        neuralNetworkOptions.setMinError(MIN_ERROR);
        neuralNetworkOptions.setNumInNeurons(NUM_IN_NEURONS);
        neuralNetworkOptions.setNumOutNeurons(NUM_OUT_NEURONS);
        neuralNetworkOptions.setBackPropagationOptions(new BackPropagationOptions());
        neuralNetworkOptions.getBackPropagationOptions().setMomentum(MOMENTUM);
        neuralNetworkOptions.getBackPropagationOptions().setLearningRate(LEARNING_RATE);
        return neuralNetworkOptions;
    }

    /**
     * Creates J48 options.
     *
     * @return J48 options
     */
    public static J48Options createJ48Options() {
        J48Options j48Options = new J48Options();
        j48Options.setNumFolds(NUM_FOLDS);
        j48Options.setMinNumObj(NUM_OBJ);
        j48Options.setBinarySplits(true);
        j48Options.setUnpruned(false);
        return j48Options;
    }

    /**
     * Creates KNN options.
     *
     * @return KNN options
     */
    public static KNearestNeighboursOptions createKNearestNeighboursOptions() {
        KNearestNeighboursOptions options = new KNearestNeighboursOptions();
        options.setNumNeighbours(NUM_NEIGHBOURS);
        options.setWeight(KNN_WEIGHT);
        return options;
    }

    /**
     * Creates random forests options.
     *
     * @param decisionTreeType - decision tree type
     * @return random forests options
     */
    public static RandomForestsOptions createRandomForestsOptions(DecisionTreeType decisionTreeType) {
        RandomForestsOptions options = new RandomForestsOptions();
        options.setDecisionTreeType(decisionTreeType);
        options.setMinObj(NUM_OBJ);
        options.setMaxDepth(MAX_DEPTH);
        options.setNumRandomAttr(NUM_RANDOM_ATTR);
        options.setNumIterations(NUM_ITERATIONS);
        options.setSeed(SEED);
        return options;
    }

    /**
     * Creates extra trees options.
     *
     * @param decisionTreeType - decision tree type
     * @return extra trees options
     */
    public static ExtraTreesOptions createExtraTreesOptions(DecisionTreeType decisionTreeType) {
        ExtraTreesOptions options = new ExtraTreesOptions();
        options.setDecisionTreeType(decisionTreeType);
        options.setMinObj(NUM_OBJ);
        options.setMaxDepth(MAX_DEPTH);
        options.setNumRandomAttr(NUM_RANDOM_ATTR);
        options.setNumIterations(NUM_ITERATIONS);
        options.setSeed(SEED);
        options.setUseBootstrapSamples(true);
        options.setNumRandomSplits(NUM_RANDOM_SPLITS);
        return options;
    }

    /**
     * Creates random networks options.
     *
     * @return random networks options
     */
    public static RandomNetworkOptions createRandomNetworkOptions() {
        RandomNetworkOptions randomNetworkOptions = new RandomNetworkOptions();
        randomNetworkOptions.setUseBootstrapSamples(true);
        randomNetworkOptions.setNumIterations(NUM_ITERATIONS);
        randomNetworkOptions.setSeed(SEED);
        randomNetworkOptions.setMinError(MIN_ERROR_THRESHOLD);
        randomNetworkOptions.setMaxError(MAX_ERROR_THRESHOLD);
        return randomNetworkOptions;
    }

    /**
     * Creates stacking options.
     *
     * @return stacking options
     */
    public static StackingOptions createStackingOptions() {
        StackingOptions stackingOptions = new StackingOptions();
        stackingOptions.setSeed(SEED);
        stackingOptions.setNumFolds(NUM_FOLDS);
        stackingOptions.setUseCrossValidation(true);
        stackingOptions.setClassifierOptions(createClassifierOptions());
        stackingOptions.setMetaClassifierOptions(createJ48Options());
        return stackingOptions;
    }

    /**
     * Creates AdaBoost options.
     *
     * @return AdaBoost options
     */
    public static AdaBoostOptions createAdaBoostOptions() {
        AdaBoostOptions options = new AdaBoostOptions();
        options.setNumIterations(NUM_ITERATIONS);
        options.setSeed(SEED);
        options.setMinError(MIN_ERROR_THRESHOLD);
        options.setMaxError(MAX_ERROR_THRESHOLD);
        options.setClassifierOptions(createClassifierOptions());
        return options;
    }

    /**
     * Creates HEC options.
     *
     * @param useRandomSubspaces - is use random subspaces sampling at each iteration?
     * @return HEC options
     */
    public static HeterogeneousClassifierOptions createHeterogeneousClassifierOptions(boolean useRandomSubspaces) {
        HeterogeneousClassifierOptions options = new HeterogeneousClassifierOptions();
        options.setUseRandomSubspaces(useRandomSubspaces);
        options.setSeed(SEED);
        options.setNumIterations(NUM_ITERATIONS);
        options.setUseWeightedVotes(true);
        options.setSamplingMethod(SamplingMethod.BAGGING);
        options.setUseRandomClassifier(true);
        options.setMinError(MIN_ERROR_THRESHOLD);
        options.setMaxError(MAX_ERROR_THRESHOLD);
        options.setClassifierOptions(createClassifierOptions());
        return options;
    }

    /**
     * Creates classifiers options list.
     *
     * @return classifiers options list
     */
    public static List<ClassifierOptions> createClassifierOptions() {
        List<ClassifierOptions> classifierOptions = new ArrayList<>();
        classifierOptions.add(createJ48Options());
        DecisionTreeOptions treeOptions = createDecisionTreeOptions();
        treeOptions.setDecisionTreeType(DecisionTreeType.C45);
        classifierOptions.add(treeOptions);
        return classifierOptions;
    }
}
