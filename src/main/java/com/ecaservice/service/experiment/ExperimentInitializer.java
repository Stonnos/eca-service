package com.ecaservice.service.experiment;


import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.experiment.ExperimentTypeVisitor;
import com.ecaservice.model.experiment.InitializationParams;
import eca.dataminer.*;
import eca.ensemble.*;
import eca.metrics.KNearestNeighbours;
import eca.neural.NeuralNetwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExperimentInitializer implements ExperimentTypeVisitor<AbstractExperiment, InitializationParams> {

    private final ExperimentConfig experimentConfig;
    private final CrossValidationConfig crossValidationConfig;

    @Autowired
    public ExperimentInitializer(ExperimentConfig experimentConfig,
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
        ClassifiersSet classifiersSet = ClassifiersSetBuilder.createClassifiersSet(initializationParams.getData(),
                experimentConfig.getMaximumFractionDigits());
        HeterogeneousClassifier heterogeneousClassifier = new HeterogeneousClassifier(classifiersSet);
        heterogeneousClassifier.setIterationsNum(experimentConfig.getEnsemble().getNumIterations());
        return new AutomatedHeterogeneousEnsemble(heterogeneousClassifier, initializationParams.getData());
    }

    @Override
    public AbstractExperiment caseModifiedHeterogeneousEnsemble(InitializationParams initializationParams) {
        ClassifiersSet classifiersSet = ClassifiersSetBuilder.createClassifiersSet(initializationParams.getData(),
                experimentConfig.getMaximumFractionDigits());
        ModifiedHeterogeneousClassifier modifiedHeterogeneousClassifier =
                new ModifiedHeterogeneousClassifier(classifiersSet);
        modifiedHeterogeneousClassifier.setIterationsNum(experimentConfig.getEnsemble().getNumIterations());
        return new AutomatedHeterogeneousEnsemble(modifiedHeterogeneousClassifier, initializationParams.getData());
    }

    @Override
    public AbstractExperiment caseAdaBoost(InitializationParams initializationParams) {
        ClassifiersSet classifiersSet = ClassifiersSetBuilder.createClassifiersSet(initializationParams.getData(),
                experimentConfig.getMaximumFractionDigits());
        AdaBoostClassifier adaBoostClassifier = new AdaBoostClassifier(classifiersSet);
        adaBoostClassifier.setIterationsNum(experimentConfig.getEnsemble().getNumIterations());
        return new AutomatedHeterogeneousEnsemble(adaBoostClassifier, initializationParams.getData());
    }

    @Override
    public AbstractExperiment caseStacking(InitializationParams initializationParams) {
        ClassifiersSet classifiersSet = ClassifiersSetBuilder.createClassifiersSet(initializationParams.getData(),
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
