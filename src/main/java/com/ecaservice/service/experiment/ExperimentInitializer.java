package com.ecaservice.service.experiment;


import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.ExperimentTypeVisitor;
import eca.dataminer.*;
import eca.ensemble.AdaBoostClassifier;
import eca.ensemble.HeterogeneousClassifier;
import eca.ensemble.ModifiedHeterogeneousClassifier;
import eca.ensemble.StackingClassifier;
import eca.metrics.KNearestNeighbours;
import eca.neural.NeuralNetwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import weka.core.Instances;

@Component
public class ExperimentInitializer implements ExperimentTypeVisitor<AbstractExperiment, Instances> {

    private final ExperimentConfig experimentConfig;
    private final CrossValidationConfig crossValidationConfig;

    @Autowired
    public ExperimentInitializer(ExperimentConfig experimentConfig,
                                 CrossValidationConfig crossValidationConfig) {
        this.experimentConfig = experimentConfig;
        this.crossValidationConfig = crossValidationConfig;
    }

    @Override
    public AbstractExperiment caseNeuralNetwork(Instances data) {
        AutomatedNeuralNetwork automatedNeuralNetwork = new AutomatedNeuralNetwork(data, new NeuralNetwork(data));
        automatedNeuralNetwork.setNumIterations(experimentConfig.getNumIterations());
        return automatedNeuralNetwork;
    }

    @Override
    public AbstractExperiment caseHeterogeneousEnsemble(Instances data) {
        return new AutomatedHeterogeneousEnsemble(new HeterogeneousClassifier(), data);
    }

    @Override
    public AbstractExperiment caseModifiedHeterogeneousEnsemble(Instances data) {
        return new AutomatedHeterogeneousEnsemble(new ModifiedHeterogeneousClassifier(), data);
    }

    @Override
    public AbstractExperiment caseAdaBoost(Instances data) {
        return new AutomatedHeterogeneousEnsemble(new AdaBoostClassifier(), data);
    }

    @Override
    public AbstractExperiment caseStacking(Instances data) {
        return new AutomatedStacking(new StackingClassifier(), data);
    }

    @Override
    public AbstractExperiment caseKNearestNeighbours(Instances data) {
        AutomatedKNearestNeighbours automatedKNearestNeighbours =
                new AutomatedKNearestNeighbours(data, new KNearestNeighbours());
        automatedKNearestNeighbours.setNumIterations(experimentConfig.getNumIterations());
        return automatedKNearestNeighbours;
    }

    private void initializeEvaluationMethodOptions(AbstractExperiment experiment) {

    }
}
