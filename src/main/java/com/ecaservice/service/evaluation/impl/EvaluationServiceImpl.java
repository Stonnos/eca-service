package com.ecaservice.service.evaluation.impl;


import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.model.*;
import com.ecaservice.model.evaluation.ClassificationResult;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.evaluation.EvaluationMethodVisitor;
import com.ecaservice.model.evaluation.EvaluationOption;
import com.ecaservice.service.evaluation.EvaluationService;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.Map;
import java.util.Random;

/**
 * @author Roman Batygin
 */
@Slf4j
@Service
public class EvaluationServiceImpl implements EvaluationService {

    private final CrossValidationConfig config;

    /**
     * Constructor with dependency spring injection.
     *
     * @param config {@link CrossValidationConfig} bean
     */
    @Autowired
    public EvaluationServiceImpl(CrossValidationConfig config) {
        this.config = config;
    }

    @Override
    public ClassificationResult evaluateModel(final InputData inputData,
                                              final EvaluationMethod evaluationMethod,
                                              final Map<EvaluationOption, String> evaluationOptionsMap) {

        ClassificationResult classificationResult = new ClassificationResult();

        try {
            Assert.notNull(inputData, "Input data is not specified!");
            Assert.notNull(inputData.getClassifier(), "Classifier is not specified!");
            Assert.notNull(inputData.getData(), "Input data is not specified!");
            Assert.notNull(evaluationMethod, "Evaluation method is not specified!");
            Assert.notNull(evaluationOptionsMap, "Evaluation options map is not specified!");

            final AbstractClassifier classifier = inputData.getClassifier();
            final Instances data = inputData.getData();
            final String classifierName = classifier.getClass().getSimpleName();

            log.info("Starting {} model evaluation.", classifierName);
            log.trace("Model evaluation starting for classifier = {}, data = {}, evaluationMethod = {}",
                    classifierName, data.relationName(), evaluationMethod);

            final Evaluation evaluation = new Evaluation(data);
            final StopWatch stopWatch = new StopWatch(String.format("Stop watching for %s", classifierName));

            evaluationMethod.handle(new EvaluationMethodVisitor() {
                @Override
                public void evaluateModel() throws Exception {
                    stopWatch.start(String.format("%s model training", classifierName));
                    classifier.buildClassifier(data);
                    stopWatch.stop();
                    stopWatch.start(String.format("%s model evaluation", classifierName));
                    evaluation.evaluateModel(classifier, data);
                    stopWatch.stop();
                }

                @Override
                public void crossValidateModel() throws Exception {
                    String numFolds = evaluationOptionsMap.get(EvaluationOption.NUM_FOLDS);
                    String numTests = evaluationOptionsMap.get(EvaluationOption.NUM_TESTS);
                    log.trace("evaluateModel: numFolds = {}, numTests = {}", numFolds, numTests);

                    int folds;
                    if (numFolds == null) {
                        log.warn("Folds number is not defined. Default folds number = {} has been used.",
                                config.getNumFolds());
                        folds = config.getNumFolds();
                    } else {
                        folds = Integer.valueOf(numFolds);
                    }

                    int tests;
                    if (numTests == null) {
                        log.warn("Tests number is not defined. Default tests number = {} has been used.",
                                config.getNumTests());
                        tests = config.getNumTests();
                    } else {
                        tests = Integer.valueOf(numTests);
                    }

                    stopWatch.start(String.format("%s model evaluation", classifierName));
                    Classifier classifierCopy = AbstractClassifier.makeCopy(classifier);
                    Random random = new Random(config.getSeed());
                    evaluation.kCrossValidateModel(classifierCopy, data, folds, tests, random);
                    stopWatch.stop();

                    stopWatch.start(String.format("%s model training", classifierName));
                    classifier.buildClassifier(data);
                    stopWatch.stop();
                }
            });

            EvaluationResults evaluationResults = new EvaluationResults(classifier, evaluation);
            classificationResult.setEvaluationResults(evaluationResults);
            classificationResult.setSuccess(true);
            log.info("Evaluation for model '{}' has been successfully finished!", classifierName);
            log.info(stopWatch.prettyPrint());

        } catch (Exception exception) {
            log.error("evaluateModel: ", exception);
            classificationResult.setErrorMessage(exception.getMessage());
        }

        return classificationResult;
    }

}
