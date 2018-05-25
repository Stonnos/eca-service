package com.ecaservice.service.experiment.visitor;


import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.mapping.EvaluationMethodMapper;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.evaluation.EvaluationOption;
import com.ecaservice.model.experiment.ExperimentTypeVisitor;
import com.ecaservice.model.experiment.InitializationParams;
import com.ecaservice.service.experiment.ClassifiersSetSearcher;
import eca.dataminer.AbstractExperiment;
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
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Implements experiment parameters initialization.
 *
 * @author Roman Batygin
 */
@Component
public class ExperimentInitializationVisitor
        implements ExperimentTypeVisitor<AbstractExperiment, InitializationParams> {

    private final ExperimentConfig experimentConfig;
    private final CrossValidationConfig crossValidationConfig;
    private final EvaluationMethodMapper evaluationMethodMapper;
    private final ClassifiersSetSearcher classifiersSetSearcher;

    /**
     * Constructor with dependency spring injection.
     *
     * @param experimentConfig       - experiment config bean
     * @param crossValidationConfig  - cross validation config bean
     * @param evaluationMethodMapper - evaluation method mapper bean
     * @param classifiersSetSearcher - classifiers set searcher bean
     */
    @Inject
    public ExperimentInitializationVisitor(ExperimentConfig experimentConfig,
                                           CrossValidationConfig crossValidationConfig,
                                           EvaluationMethodMapper evaluationMethodMapper,
                                           ClassifiersSetSearcher classifiersSetSearcher) {
        this.experimentConfig = experimentConfig;
        this.crossValidationConfig = crossValidationConfig;
        this.evaluationMethodMapper = evaluationMethodMapper;
        this.classifiersSetSearcher = classifiersSetSearcher;
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
        ClassifiersSet classifiersSet = classifiersSetSearcher.findBestClassifiers(initializationParams.getData(),
                initializationParams.getEvaluationMethod(), createEvaluationOptionsMap(initializationParams));
        HeterogeneousClassifier heterogeneousClassifier = new HeterogeneousClassifier(classifiersSet);
        heterogeneousClassifier.setNumThreads(getNumThreads());
        heterogeneousClassifier.setIterationsNum(experimentConfig.getEnsemble().getNumIterations());
        return new AutomatedHeterogeneousEnsemble(heterogeneousClassifier, initializationParams.getData());
    }

    @Override
    public AbstractExperiment caseModifiedHeterogeneousEnsemble(InitializationParams initializationParams) {
        ClassifiersSet classifiersSet = classifiersSetSearcher.findBestClassifiers(initializationParams.getData(),
                initializationParams.getEvaluationMethod(), createEvaluationOptionsMap(initializationParams));
        ModifiedHeterogeneousClassifier modifiedHeterogeneousClassifier =
                new ModifiedHeterogeneousClassifier(classifiersSet);
        modifiedHeterogeneousClassifier.setNumThreads(getNumThreads());
        modifiedHeterogeneousClassifier.setIterationsNum(experimentConfig.getEnsemble().getNumIterations());
        return new AutomatedHeterogeneousEnsemble(modifiedHeterogeneousClassifier, initializationParams.getData());
    }

    @Override
    public AbstractExperiment caseAdaBoost(InitializationParams initializationParams) {
        ClassifiersSet classifiersSet = classifiersSetSearcher.findBestClassifiers(initializationParams.getData(),
                initializationParams.getEvaluationMethod(), createEvaluationOptionsMap(initializationParams));
        AdaBoostClassifier adaBoostClassifier = new AdaBoostClassifier(classifiersSet);
        adaBoostClassifier.setIterationsNum(experimentConfig.getEnsemble().getNumIterations());
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
    public void afterHandle(AbstractExperiment experiment, InitializationParams initializationParams) {
        experiment.setEvaluationMethod(evaluationMethodMapper.map(initializationParams.getEvaluationMethod()));
        experiment.setNumFolds(crossValidationConfig.getNumFolds());
        experiment.setNumTests(crossValidationConfig.getNumTests());
        experiment.setSeed(crossValidationConfig.getSeed());
    }

    private AutomatedStacking createStackingExperiment(InitializationParams initializationParams,
                                                       boolean useCrossValidation) {
        ClassifiersSet classifiersSet = classifiersSetSearcher.findBestClassifiers(initializationParams.getData(),
                initializationParams.getEvaluationMethod(), createEvaluationOptionsMap(initializationParams));
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

    private Map<EvaluationOption, String> createEvaluationOptionsMap(InitializationParams initializationParams) {
        if (EvaluationMethod.CROSS_VALIDATION.equals(initializationParams.getEvaluationMethod())) {
            Map<EvaluationOption, String> evaluationOptionStringMap = new EnumMap<>(EvaluationOption.class);
            evaluationOptionStringMap.put(EvaluationOption.NUM_FOLDS,
                    String.valueOf(crossValidationConfig.getNumFolds()));
            evaluationOptionStringMap.put(EvaluationOption.NUM_TESTS,
                    String.valueOf(crossValidationConfig.getNumTests()));
            return evaluationOptionStringMap;
        } else {
            return Collections.emptyMap();
        }
    }

}
