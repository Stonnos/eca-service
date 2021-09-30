package com.ecaservice.server.service.experiment.visitor;

import com.ecaservice.base.model.visitor.ExperimentTypeVisitor;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.model.experiment.InitializationParams;
import com.ecaservice.server.service.experiment.ClassifiersSetSearcher;
import eca.dataminer.AbstractExperiment;
import eca.dataminer.AutomatedDecisionTree;
import eca.dataminer.AutomatedHeterogeneousEnsemble;
import eca.dataminer.AutomatedKNearestNeighbours;
import eca.dataminer.AutomatedNeuralNetwork;
import eca.dataminer.AutomatedRandomForests;
import eca.dataminer.AutomatedStacking;
import eca.ensemble.AdaBoostClassifier;
import eca.ensemble.ClassifiersSet;
import eca.ensemble.HeterogeneousClassifier;
import eca.ensemble.ModifiedHeterogeneousClassifier;
import eca.ensemble.StackingClassifier;
import eca.metrics.KNearestNeighbours;
import eca.neural.NeuralNetwork;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Implements experiment parameters initialization.
 *
 * @author Roman Batygin
 */
@Component
@RequiredArgsConstructor
public class ExperimentInitializationVisitor
        implements ExperimentTypeVisitor<AbstractExperiment, InitializationParams> {

    private final ExperimentConfig experimentConfig;
    private final CrossValidationConfig crossValidationConfig;
    private final ClassifiersSetSearcher classifiersSetSearcher;

    @Override
    public AbstractExperiment caseNeuralNetwork(InitializationParams initializationParams) {
        NeuralNetwork neuralNetwork = new NeuralNetwork(initializationParams.getData());
        neuralNetwork.getDecimalFormat().setMaximumFractionDigits(experimentConfig.getMaximumFractionDigits());
        AutomatedNeuralNetwork automatedNeuralNetwork =
                new AutomatedNeuralNetwork(initializationParams.getData(), neuralNetwork);
        automatedNeuralNetwork.setNumIterations(experimentConfig.getNumIterations());
        return automatedNeuralNetwork;
    }

    @Override
    public AbstractExperiment caseHeterogeneousEnsemble(InitializationParams initializationParams) {
        ClassifiersSet classifiersSet = classifiersSetSearcher.findBestClassifiers(initializationParams.getData(),
                initializationParams.getEvaluationMethod());
        HeterogeneousClassifier heterogeneousClassifier = new HeterogeneousClassifier(classifiersSet);
        heterogeneousClassifier.setNumThreads(getNumThreads());
        heterogeneousClassifier.setNumIterations(experimentConfig.getEnsemble().getNumIterations());
        return new AutomatedHeterogeneousEnsemble(heterogeneousClassifier, initializationParams.getData());
    }

    @Override
    public AbstractExperiment caseModifiedHeterogeneousEnsemble(InitializationParams initializationParams) {
        ClassifiersSet classifiersSet = classifiersSetSearcher.findBestClassifiers(initializationParams.getData(),
                initializationParams.getEvaluationMethod());
        ModifiedHeterogeneousClassifier modifiedHeterogeneousClassifier =
                new ModifiedHeterogeneousClassifier(classifiersSet);
        modifiedHeterogeneousClassifier.setNumThreads(getNumThreads());
        modifiedHeterogeneousClassifier.setNumIterations(experimentConfig.getEnsemble().getNumIterations());
        return new AutomatedHeterogeneousEnsemble(modifiedHeterogeneousClassifier, initializationParams.getData());
    }

    @Override
    public AbstractExperiment caseAdaBoost(InitializationParams initializationParams) {
        ClassifiersSet classifiersSet = classifiersSetSearcher.findBestClassifiers(initializationParams.getData(),
                initializationParams.getEvaluationMethod());
        AdaBoostClassifier adaBoostClassifier = new AdaBoostClassifier(classifiersSet);
        adaBoostClassifier.setNumIterations(experimentConfig.getEnsemble().getNumIterations());
        return new AutomatedHeterogeneousEnsemble(adaBoostClassifier, initializationParams.getData());
    }

    @Override
    public AbstractExperiment caseStacking(InitializationParams initializationParams) {
        return createStackingExperiment(initializationParams, false);
    }

    @Override
    public AbstractExperiment caseKNearestNeighbours(InitializationParams initializationParams) {
        KNearestNeighbours kNearestNeighbours = new KNearestNeighbours();
        kNearestNeighbours.getDecimalFormat().setMaximumFractionDigits(experimentConfig.getMaximumFractionDigits());
        AutomatedKNearestNeighbours automatedKNearestNeighbours =
                new AutomatedKNearestNeighbours(initializationParams.getData(), kNearestNeighbours);
        automatedKNearestNeighbours.setNumIterations(experimentConfig.getNumIterations());
        return automatedKNearestNeighbours;
    }

    @Override
    public AbstractExperiment caseRandomForests(InitializationParams initializationParams) {
        AutomatedRandomForests automatedRandomForests = new AutomatedRandomForests(initializationParams.getData());
        automatedRandomForests.setNumIterations(experimentConfig.getNumIterations());
        automatedRandomForests.setNumThreads(getNumThreads());
        return automatedRandomForests;
    }

    @Override
    public AbstractExperiment caseStackingCV(InitializationParams initializationParams) {
        AutomatedStacking automatedStacking = createStackingExperiment(initializationParams, true);
        automatedStacking.getClassifier().setNumFolds(experimentConfig.getEnsemble().getNumFoldsForStacking());
        return automatedStacking;
    }

    @Override
    public AbstractExperiment caseDecisionTree(InitializationParams initializationParams) {
        AutomatedDecisionTree automatedDecisionTree = new AutomatedDecisionTree(initializationParams.getData());
        automatedDecisionTree.setNumIterations(experimentConfig.getNumIterations());
        return automatedDecisionTree;
    }

    @Override
    public void afterHandle(AbstractExperiment experiment, InitializationParams initializationParams) {
        experiment.setEvaluationMethod(initializationParams.getEvaluationMethod());
        experiment.setNumFolds(crossValidationConfig.getNumFolds());
        experiment.setNumTests(crossValidationConfig.getNumTests());
        experiment.setSeed(crossValidationConfig.getSeed());
    }

    private AutomatedStacking createStackingExperiment(InitializationParams initializationParams,
                                                       boolean useCrossValidation) {
        ClassifiersSet classifiersSet = classifiersSetSearcher.findBestClassifiers(initializationParams.getData(),
                initializationParams.getEvaluationMethod());
        StackingClassifier stackingClassifier = new StackingClassifier(useCrossValidation);
        stackingClassifier.setClassifiers(classifiersSet);
        return new AutomatedStacking(stackingClassifier, initializationParams.getData());
    }

    /**
     * Returns specified threads number for concurrent algorithms. If threads number isn't specified then
     * returns available JVM processors number.
     *
     * @return threads number for concurrent algorithms
     */
    private Integer getNumThreads() {
        if (Boolean.TRUE.equals(experimentConfig.getEnsemble().getMultiThreadModeEnabled())) {
            return experimentConfig.getEnsemble().getNumThreads() != null ?
                    experimentConfig.getEnsemble().getNumThreads() : Runtime.getRuntime().availableProcessors();
        } else {
            return null;
        }
    }
}
