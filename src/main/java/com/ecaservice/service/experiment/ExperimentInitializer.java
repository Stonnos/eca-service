package com.ecaservice.service.experiment;


import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.ExperimentTypeVisitor;
import eca.dataminer.*;
import eca.ensemble.*;
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
        NeuralNetwork neuralNetwork = new NeuralNetwork(data);
        neuralNetwork.getDecimalFormat().setMaximumFractionDigits(experimentConfig.getMaximumFractionDigits());
        AutomatedNeuralNetwork automatedNeuralNetwork = new AutomatedNeuralNetwork(data, neuralNetwork);
        automatedNeuralNetwork.setNumIterations(experimentConfig.getNumIterations());
        return automatedNeuralNetwork;
    }

    @Override
    public AbstractExperiment caseHeterogeneousEnsemble(Instances data) {
        ClassifiersSet classifiersSet = ClassifiersSetBuilder.createClassifiersSet(data,
                experimentConfig.getMaximumFractionDigits());
        return new AutomatedHeterogeneousEnsemble(new HeterogeneousClassifier(classifiersSet), data);
    }

    @Override
    public AbstractExperiment caseModifiedHeterogeneousEnsemble(Instances data) {
        ClassifiersSet classifiersSet = ClassifiersSetBuilder.createClassifiersSet(data,
                experimentConfig.getMaximumFractionDigits());
        return new AutomatedHeterogeneousEnsemble(new ModifiedHeterogeneousClassifier(classifiersSet), data);
    }

    @Override
    public AbstractExperiment caseAdaBoost(Instances data) {
        ClassifiersSet classifiersSet = ClassifiersSetBuilder.createClassifiersSet(data,
                experimentConfig.getMaximumFractionDigits());
        return new AutomatedHeterogeneousEnsemble(new AdaBoostClassifier(classifiersSet), data);
    }

    @Override
    public AbstractExperiment caseStacking(Instances data) {
        ClassifiersSet classifiersSet = ClassifiersSetBuilder.createClassifiersSet(data,
                experimentConfig.getMaximumFractionDigits());
        StackingClassifier stackingClassifier = new StackingClassifier();
        stackingClassifier.setClassifiers(classifiersSet);
        return new AutomatedStacking(stackingClassifier, data);
    }

    @Override
    public AbstractExperiment caseKNearestNeighbours(Instances data) {
        KNearestNeighbours kNearestNeighbours = new KNearestNeighbours();
        kNearestNeighbours.getDecimalFormat().setMaximumFractionDigits(experimentConfig.getMaximumFractionDigits());
        AutomatedKNearestNeighbours automatedKNearestNeighbours =
                new AutomatedKNearestNeighbours(data, kNearestNeighbours);
        automatedKNearestNeighbours.setNumIterations(experimentConfig.getNumIterations());
        return automatedKNearestNeighbours;
    }

    private void initializeEvaluationMethodOptions(AbstractExperiment experiment) {

    }
}
