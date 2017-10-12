package com.ecaservice.service.experiment.visitors;


import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.experiment.ExperimentTypeVisitor;
import com.ecaservice.model.experiment.InitializationParams;
import eca.dataminer.AbstractExperiment;
import eca.dataminer.AutomatedHeterogeneousEnsemble;
import eca.dataminer.AutomatedKNearestNeighbours;
import eca.dataminer.AutomatedNeuralNetwork;
import eca.dataminer.AutomatedStacking;
import eca.dataminer.ExperimentUtil;
import eca.ensemble.AdaBoostClassifier;
import eca.ensemble.ClassifiersSet;
import eca.ensemble.HeterogeneousClassifier;
import eca.ensemble.ModifiedHeterogeneousClassifier;
import eca.ensemble.StackingClassifier;
import eca.metrics.KNearestNeighbours;
import eca.neural.NeuralNetwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Implements experiment parameters initialization.
 * @author Roman Batygin
 */
@Component
public class ExperimentInitializationVisitor implements ExperimentTypeVisitor<AbstractExperiment, InitializationParams> {

    private final ExperimentConfig experimentConfig;
    private final CrossValidationConfig crossValidationConfig;

    /**
     * Constructor with dependency spring injection.
     * @param experimentConfig {@link ExperimentConfig} bean
     * @param crossValidationConfig {@link CrossValidationConfig} bean
     */
    @Autowired
    public ExperimentInitializationVisitor(ExperimentConfig experimentConfig,
                                           CrossValidationConfig crossValidationConfig) {
        this.experimentConfig = experimentConfig;
        this.crossValidationConfig = crossValidationConfig;
    }

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
        ClassifiersSet classifiersSet = ExperimentUtil.builtClassifiersSet(initializationParams.getData(),
                experimentConfig.getMaximumFractionDigits());
        HeterogeneousClassifier heterogeneousClassifier = new HeterogeneousClassifier(classifiersSet);
        heterogeneousClassifier.setIterationsNum(experimentConfig.getEnsemble().getNumIterations());
        return new AutomatedHeterogeneousEnsemble(heterogeneousClassifier, initializationParams.getData());
    }

    @Override
    public AbstractExperiment caseModifiedHeterogeneousEnsemble(InitializationParams initializationParams) {
        ClassifiersSet classifiersSet = ExperimentUtil.builtClassifiersSet(initializationParams.getData(),
                experimentConfig.getMaximumFractionDigits());
        ModifiedHeterogeneousClassifier modifiedHeterogeneousClassifier =
                new ModifiedHeterogeneousClassifier(classifiersSet);
        modifiedHeterogeneousClassifier.setIterationsNum(experimentConfig.getEnsemble().getNumIterations());
        return new AutomatedHeterogeneousEnsemble(modifiedHeterogeneousClassifier, initializationParams.getData());
    }

    @Override
    public AbstractExperiment caseAdaBoost(InitializationParams initializationParams) {
        ClassifiersSet classifiersSet = ExperimentUtil.builtClassifiersSet(initializationParams.getData(),
                experimentConfig.getMaximumFractionDigits());
        AdaBoostClassifier adaBoostClassifier = new AdaBoostClassifier(classifiersSet);
        adaBoostClassifier.setIterationsNum(experimentConfig.getEnsemble().getNumIterations());
        return new AutomatedHeterogeneousEnsemble(adaBoostClassifier, initializationParams.getData());
    }

    @Override
    public AbstractExperiment caseStacking(InitializationParams initializationParams) {
        ClassifiersSet classifiersSet = ExperimentUtil.builtClassifiersSet(initializationParams.getData(),
                experimentConfig.getMaximumFractionDigits());
        StackingClassifier stackingClassifier = new StackingClassifier();
        stackingClassifier.setClassifiers(classifiersSet);
        return new AutomatedStacking(stackingClassifier, initializationParams.getData());
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
    public void afterHandle(AbstractExperiment experiment, InitializationParams initializationParams) {
        experiment.setEvaluationMethod(initializationParams.getEvaluationMethod());
        experiment.setNumFolds(crossValidationConfig.getNumFolds());
        experiment.setNumTests(crossValidationConfig.getNumTests());
    }

}
