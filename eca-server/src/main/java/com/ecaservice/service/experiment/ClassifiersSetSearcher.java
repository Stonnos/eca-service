package com.ecaservice.service.experiment;

import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.exception.ExperimentException;
import com.ecaservice.mapping.options.ClassifierOptionsMapper;
import com.ecaservice.model.InputData;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.evaluation.ClassificationResult;
import com.ecaservice.model.evaluation.EvaluationOption;
import com.ecaservice.model.options.ClassifierOptions;
import com.ecaservice.service.evaluation.EvaluationService;
import com.ecaservice.service.experiment.handler.ClassifierInputDataHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.core.evaluation.EvaluationMethod;
import eca.core.evaluation.EvaluationResults;
import eca.dataminer.ClassifierComparator;
import eca.ensemble.ClassifiersSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;
import weka.core.Randomizable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ecaservice.util.ExperimentLogUtils.logAndThrowError;

/**
 * Service for searching the best individual classifiers set by the criterion of accuracy maximization.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ClassifiersSetSearcher {

    private static ObjectMapper objectMapper = new ObjectMapper();

    private final EvaluationService evaluationService;
    private final ExperimentConfigurationService experimentConfigurationService;
    private final ExperimentConfig experimentConfig;
    private final CrossValidationConfig crossValidationConfig;
    private final List<ClassifierInputDataHandler> classifierInputDataHandlers;
    private final List<ClassifierOptionsMapper> classifierOptionsMappers;

    /**
     * Constructor with spring dependency injection.
     *
     * @param evaluationService              - evaluation service bean
     * @param experimentConfigurationService - experiment configuration service bean
     * @param experimentConfig               - experiment config bean
     * @param crossValidationConfig          - cross - validation config
     * @param classifierInputDataHandlers    - classifier input data handler beans
     * @param classifierOptionsMappers       - classifier options mapper bean
     */
    @Inject
    public ClassifiersSetSearcher(EvaluationService evaluationService,
                                  ExperimentConfigurationService experimentConfigurationService,
                                  ExperimentConfig experimentConfig,
                                  CrossValidationConfig crossValidationConfig,
                                  List<ClassifierInputDataHandler> classifierInputDataHandlers,
                                  List<ClassifierOptionsMapper> classifierOptionsMappers) {
        this.evaluationService = evaluationService;
        this.experimentConfigurationService = experimentConfigurationService;
        this.experimentConfig = experimentConfig;
        this.crossValidationConfig = crossValidationConfig;
        this.classifierInputDataHandlers = classifierInputDataHandlers;
        this.classifierOptionsMappers = classifierOptionsMappers;
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
        List<AbstractClassifier> classifiersSet = readClassifiers();
        initializeClassifiers(classifiersSet, data);
        ArrayList<EvaluationResults> finished = new ArrayList<>(classifiersSet.size());

        for (AbstractClassifier classifier : classifiersSet) {
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

    private List<AbstractClassifier> readClassifiers() {
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels = experimentConfigurationService
                .findLastClassifiersOptions();
        if (CollectionUtils.isEmpty(classifierOptionsDatabaseModels)) {
            throw new ExperimentException("Classifiers options config hasn't been found.");
        }
        log.info("{} classifiers configs has been found.", classifierOptionsDatabaseModels.size());
        List<AbstractClassifier> classifierList = new ArrayList<>(classifierOptionsDatabaseModels.size());
        try {
            for (ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel : classifierOptionsDatabaseModels) {
                ClassifierOptions classifierOptions =
                        objectMapper.readValue(classifierOptionsDatabaseModel.getConfig(), ClassifierOptions.class);
                for (ClassifierOptionsMapper optionsMapper : classifierOptionsMappers) {
                    if (optionsMapper.canMap(classifierOptions)) {
                        classifierList.add(optionsMapper.map(classifierOptions));
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            logAndThrowError(String.format("There was an error while creating individual classifiers: %s",
                    ex.getMessage()), log);
        }
        log.info("{} individual classifiers has been successfully created.",
                classifierList.size());
        return classifierList;
    }

    private void initializeClassifiers(List<AbstractClassifier> classifiers, Instances data) {
        //Initialize classifiers options based on training data
        for (AbstractClassifier classifier : classifiers) {
            if (classifier instanceof Randomizable) {
                ((Randomizable) classifier).setSeed(crossValidationConfig.getSeed());
            }
            for (ClassifierInputDataHandler handler : classifierInputDataHandlers) {
                if (handler.canHandle(classifier)) {
                    handler.handle(data, classifier);
                    break;
                }
            }
        }
    }
}
