package com.ecaservice.server.service.experiment;

import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.exception.experiment.ExperimentException;
import com.ecaservice.server.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.server.model.evaluation.EvaluationInputDataModel;
import com.ecaservice.server.service.classifiers.ClassifierOptionsService;
import com.ecaservice.server.service.evaluation.EvaluationService;
import com.ecaservice.server.service.experiment.handler.ClassifierInputDataHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.core.evaluation.EvaluationMethod;
import eca.core.evaluation.EvaluationResults;
import eca.dataminer.ClassifierComparator;
import eca.ensemble.ClassifiersSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.micrometer.tracing.annotation.NewSpan;
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

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final EvaluationService evaluationService;
    private final ClassifierOptionsService classifierOptionsService;
    private final ExperimentConfig experimentConfig;
    private final CrossValidationConfig crossValidationConfig;
    private final List<ClassifierInputDataHandler> classifierInputDataHandlers;
    private final ClassifierOptionsAdapter classifierOptionsAdapter;

    /**
     * Finds the best individual classifiers set by the criterion of accuracy maximization.
     * Individual classifiers set are taken from database and then are cached on time specified in configs.
     *
     * @param data             training data
     * @param evaluationMethod evaluation method
     * @return classifiers set
     */
    @NewSpan
    public ClassifiersSet findBestClassifiers(Instances data, EvaluationMethod evaluationMethod) {
        log.info("Starting to find the best individual classifiers using {} evaluation method.", evaluationMethod);
        List<AbstractClassifier> classifiersSet = readClassifiers();
        initializeClassifiers(classifiersSet, data);
        ArrayList<EvaluationResults> finished = new ArrayList<>(classifiersSet.size());

        for (AbstractClassifier classifier : classifiersSet) {
            var evaluationInputDataModel =
                    createEvaluationInputDataModel(classifier, data, evaluationMethod);
            var evaluationResults = evaluationService.evaluateModel(evaluationInputDataModel);
            evaluationResults.setClassifier(classifier);
            finished.add(evaluationResults);
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
                classifiers.toList()
                        .stream()
                        .map(classifier -> classifier.getClass().getSimpleName())
                        .collect(Collectors.toList()));
        return classifiers;
    }

    private List<AbstractClassifier> readClassifiers() {
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels =
                classifierOptionsService.getActiveClassifiersOptions();
        if (CollectionUtils.isEmpty(classifierOptionsDatabaseModels)) {
            throw new ExperimentException("Expected not empty classifiers options list.");
        }
        List<AbstractClassifier> classifierList = new ArrayList<>(classifierOptionsDatabaseModels.size());
        try {
            for (ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel : classifierOptionsDatabaseModels) {
                ClassifierOptions classifierOptions =
                        OBJECT_MAPPER.readValue(classifierOptionsDatabaseModel.getConfig(), ClassifierOptions.class);
                classifierList.add(classifierOptionsAdapter.convert(classifierOptions));
            }
        } catch (Exception ex) {
            throw new ExperimentException(
                    String.format("There was an error while creating individual classifiers: %s", ex.getMessage()));
        }
        log.info("{} individual classifiers has been successfully created.",
                classifierList.size());
        return classifierList;
    }

    @SuppressWarnings("unchecked")
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

    private EvaluationInputDataModel createEvaluationInputDataModel(AbstractClassifier classifier,
                                                                    Instances data,
                                                                    EvaluationMethod evaluationMethod) {
        EvaluationInputDataModel evaluationRequestDataModel = new EvaluationInputDataModel();
        evaluationRequestDataModel.setClassifier(classifier);
        evaluationRequestDataModel.setData(data);
        evaluationRequestDataModel.setEvaluationMethod(evaluationMethod);
        if (EvaluationMethod.CROSS_VALIDATION.equals(evaluationMethod)) {
            evaluationRequestDataModel.setNumFolds(crossValidationConfig.getNumFolds());
            evaluationRequestDataModel.setNumTests(crossValidationConfig.getNumTests());
            evaluationRequestDataModel.setSeed(crossValidationConfig.getSeed());
        }
        return evaluationRequestDataModel;
    }
}
