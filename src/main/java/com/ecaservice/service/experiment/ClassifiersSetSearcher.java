package com.ecaservice.service.experiment;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.exception.ExperimentException;
import com.ecaservice.model.InputData;
import com.ecaservice.model.evaluation.ClassificationResult;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.evaluation.EvaluationOption;
import com.ecaservice.service.evaluation.EvaluationService;
import com.ecaservice.service.experiment.handler.ClassifierInputDataHandler;
import com.ecaservice.util.ClassifierUtils;
import eca.core.evaluation.EvaluationResults;
import eca.dataminer.ClassifierComparator;
import eca.ensemble.ClassifiersSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for searching the best individual classifiers set by the criterion of accuracy maximization.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ClassifiersSetSearcher {

    private final EvaluationService evaluationService;
    private final ExperimentConfigurationService experimentConfigurationService;
    private final ExperimentConfig experimentConfig;
    private final List<ClassifierInputDataHandler> classifierInputDataHandlers;

    /**
     * Constructor with spring dependency injection.
     *
     * @param evaluationService              {@link EvaluationService} bean
     * @param experimentConfigurationService {@link ExperimentConfigurationService} bean
     * @param experimentConfig               {@link ExperimentConfig} bean
     * @param classifierInputDataHandlers    {@link ClassifierInputDataHandler} beans
     */
    @Autowired
    public ClassifiersSetSearcher(EvaluationService evaluationService,
                                  ExperimentConfigurationService experimentConfigurationService,
                                  ExperimentConfig experimentConfig,
                                  List<ClassifierInputDataHandler> classifierInputDataHandlers) {
        this.evaluationService = evaluationService;
        this.experimentConfigurationService = experimentConfigurationService;
        this.experimentConfig = experimentConfig;
        this.classifierInputDataHandlers = classifierInputDataHandlers;
    }

    /**
     * Finds the best individual classifiers set by the criterion of accuracy maximization.
     * Individual classifiers set are taken from database and then are cached on time specified in configs.
     *
     * @param data                      training data
     * @param evaluationMethod          evaluation method
     * @param evaluationOptionStringMap evaluation options {@link Map}
     * @return classifiers set {@link ClassifiersSet}
     */
    public ClassifiersSet findBestClassifiers(Instances data, EvaluationMethod evaluationMethod,
                                              Map<EvaluationOption, String> evaluationOptionStringMap) {
        log.info("Starting to find the best individual classifiers using {} evaluation method.", evaluationMethod);
        List<AbstractClassifier> classifiersSet = experimentConfigurationService.findClassifiers();
        List<AbstractClassifier> initializedClassifiers = initializeClassifiers(classifiersSet, data);
        ArrayList<EvaluationResults> finished = new ArrayList<>(initializedClassifiers.size());

        for (AbstractClassifier classifier : initializedClassifiers) {
            ClassificationResult classificationResult =
                    evaluationService.evaluateModel(new InputData(classifier, data), evaluationMethod,
                            evaluationOptionStringMap);
            if (classificationResult.isSuccess()) {
                classificationResult.getEvaluationResults().setClassifier(classifier);
                finished.add(classificationResult.getEvaluationResults());
            }
        }

        if (CollectionUtils.isEmpty(finished)) {
            throw new ExperimentException("Can't find the best individual classifiers!");
        }

        finished.sort(new ClassifierComparator());
        ClassifiersSet classifiers = new ClassifiersSet();
        for (int i = 0; i < Integer.min(experimentConfig.getEnsemble().getNumBestClassifiers(), finished.size()); i++) {
            classifiers.addClassifier(finished.get(i).getClassifier());
        }
        log.info("{} best classifiers has been built.",
                classifiers.toList().stream().map(classifier -> classifier.getClass().getSimpleName()).collect(
                        Collectors.toList()));
        return classifiers;
    }

    private List<AbstractClassifier> initializeClassifiers(List<AbstractClassifier> classifiers, Instances data) {
        List<AbstractClassifier> classifiersCopies = ClassifierUtils.createCopies(classifiers);
        //Initialize classifiers options based on training data
        for (AbstractClassifier classifier : classifiersCopies) {
            for (ClassifierInputDataHandler handler : classifierInputDataHandlers) {
                if (handler.canHandle(classifier)) {
                    handler.handle(data, classifier);
                }
            }
        }
        return classifiersCopies;
    }
}
