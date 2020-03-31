package com.ecaservice.service.experiment;

import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.conversion.ClassifierOptionsConverter;
import com.ecaservice.dto.EvaluationRequest;
import com.ecaservice.exception.experiment.ExperimentException;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.evaluation.ClassificationResult;
import com.ecaservice.model.options.ClassifierOptions;
import com.ecaservice.service.classifiers.ClassifierOptionsService;
import com.ecaservice.service.evaluation.EvaluationService;
import com.ecaservice.service.experiment.handler.ClassifierInputDataHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.core.evaluation.EvaluationMethod;
import eca.core.evaluation.EvaluationResults;
import eca.dataminer.ClassifierComparator;
import eca.ensemble.ClassifiersSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;
import weka.core.Randomizable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for searching the best individual classifiers set by the criterion of accuracy maximization.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifiersSetSearcher {

    private static ObjectMapper objectMapper = new ObjectMapper();

    private final EvaluationService evaluationService;
    private final ClassifierOptionsService classifierOptionsService;
    private final ExperimentConfig experimentConfig;
    private final CrossValidationConfig crossValidationConfig;
    private final List<ClassifierInputDataHandler> classifierInputDataHandlers;
    private final ClassifierOptionsConverter classifierOptionsConverter;

    /**
     * Finds the best individual classifiers set by the criterion of accuracy maximization.
     * Individual classifiers set are taken from database and then are cached on time specified in configs.
     *
     * @param data             training data
     * @param evaluationMethod evaluation method
     * @return classifiers set
     */
    public ClassifiersSet findBestClassifiers(Instances data, EvaluationMethod evaluationMethod) {
        log.info("Starting to find the best individual classifiers using {} evaluation method.", evaluationMethod);
        List<AbstractClassifier> classifiersSet = readClassifiers();
        initializeClassifiers(classifiersSet, data);
        ArrayList<EvaluationResults> finished = new ArrayList<>(classifiersSet.size());

        for (AbstractClassifier classifier : classifiersSet) {
            EvaluationRequest evaluationRequest = createEvaluationRequest(classifier, data, evaluationMethod);
            ClassificationResult classificationResult = evaluationService.evaluateModel(evaluationRequest);
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
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels =
                classifierOptionsService.getActiveClassifiersOptions();
        if (CollectionUtils.isEmpty(classifierOptionsDatabaseModels)) {
            throw new ExperimentException("Classifiers options config hasn't been found.");
        }
        log.info("{} classifiers configs has been found.", classifierOptionsDatabaseModels.size());
        List<AbstractClassifier> classifierList = new ArrayList<>(classifierOptionsDatabaseModels.size());
        try {
            for (ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel : classifierOptionsDatabaseModels) {
                ClassifierOptions classifierOptions =
                        objectMapper.readValue(classifierOptionsDatabaseModel.getConfig(), ClassifierOptions.class);
                classifierList.add(classifierOptionsConverter.convert(classifierOptions));
            }
        } catch (Exception ex) {
            throw new ExperimentException(
                    String.format("There was an error while creating individual classifiers: %s", ex.getMessage()));
        }
        log.info("{} individual classifiers has been successfully created.",
                classifierList.size());
        return classifierList;
    }

    private void initializeClassifiers(List<AbstractClassifier> classifiers, Instances data) {
        //Initialize classifiers options based on training data
        classifiers.forEach(classifier -> {
            if (classifier instanceof Randomizable) {
                ((Randomizable) classifier).setSeed(crossValidationConfig.getSeed());
            }
            classifierInputDataHandlers.stream()
                    .filter(h -> h.canHandle(classifier))
                    .findFirst()
                    .ifPresent(classifierInputDataHandler -> classifierInputDataHandler.handle(data, classifier));
        });
    }

    private EvaluationRequest createEvaluationRequest(AbstractClassifier classifier, Instances data,
                                                      EvaluationMethod evaluationMethod) {
        EvaluationRequest evaluationRequest = new EvaluationRequest();
        evaluationRequest.setClassifier(classifier);
        evaluationRequest.setData(data);
        evaluationRequest.setEvaluationMethod(evaluationMethod);
        if (EvaluationMethod.CROSS_VALIDATION.equals(evaluationMethod)) {
            evaluationRequest.setNumFolds(crossValidationConfig.getNumFolds());
            evaluationRequest.setNumTests(crossValidationConfig.getNumTests());
            evaluationRequest.setSeed(crossValidationConfig.getSeed());
        }
        return evaluationRequest;
    }
}
