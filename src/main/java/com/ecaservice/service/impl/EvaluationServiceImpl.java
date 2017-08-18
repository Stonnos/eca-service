package com.ecaservice.service.impl;


import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.model.ClassificationResult;
import com.ecaservice.model.EvaluationMethod;
import com.ecaservice.service.EvaluationService;
import eca.core.evaluation.Evaluation;
import eca.model.ClassifierDescriptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.Random;

/**
 * @author Roman Batygin
 */
@Slf4j
@Service
public class EvaluationServiceImpl implements EvaluationService {

    public static final int MINIMUM_NUMBER_OF_FOLDS = 2;

    public static final int MINIMUM_NUMBER_OF_TESTS = 1;

    public static final int MAXIMUM_NUMBER_OF_FOLDS = 100;

    public static final int MAXIMUM_NUMBER_OF_TESTS = 100;

    private CrossValidationConfig config;

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
    public ClassificationResult evaluateModel(AbstractClassifier classifier,
                                              Instances data,
                                              EvaluationMethod evaluationMethod,
                                              Integer numFolds,
                                              Integer numTests) {

        Assert.notNull(classifier, "Classifier is not specified!");
        Assert.notNull(data, "Input data is not specified!");
        Assert.notNull(evaluationMethod, "Evaluation method is not specified!");

        if (evaluationMethod == EvaluationMethod.CROSS_VALIDATION) {
            if (numFolds == null) {
                log.warn("Folds number is not defined. Default folds number = {} has been used.",
                        config.getNumFolds());
                numFolds = config.getNumFolds();
            }

            Assert.isTrue(numFolds >= MINIMUM_NUMBER_OF_FOLDS,
                    String.format("The number of folds must be greater than or equal to %d!", MINIMUM_NUMBER_OF_FOLDS));

            Assert.isTrue(numFolds <= MAXIMUM_NUMBER_OF_FOLDS,
                    String.format("Number of folds must be less than or equal to %d!", MAXIMUM_NUMBER_OF_FOLDS));

            if (numTests == null) {
                log.warn("Tests number is not defined. Default tests number = {} has been used.",
                        config.getNumTests());
                numTests = config.getNumTests();
            }

            Assert.isTrue(numTests >= MINIMUM_NUMBER_OF_TESTS,
                    String.format("Number of tests must be greater than or equal to %d!", MINIMUM_NUMBER_OF_TESTS));

            Assert.isTrue(numTests <= MAXIMUM_NUMBER_OF_TESTS,
                    String.format("Number of tests must be less than or equal to %d!", MAXIMUM_NUMBER_OF_TESTS));
        }

        String classifierName = classifier.getClass().getSimpleName();

        log.info("Starting model evaluation.");

        log.trace("Model evaluation starting for classifier = {}, data = {}, evaluationMethod = {}",
                classifierName, data.relationName(), evaluationMethod);
        log.trace("numFolds = {}, numTests = {}", numFolds, numTests);

        ClassificationResult classificationResult = new ClassificationResult();

        try {
            Evaluation evaluation = new Evaluation(data);

            StopWatch stopWatch = new StopWatch();

            switch (evaluationMethod) {

                case TRAINING_DATA: {
                    stopWatch.start(String.format("%s model training", classifierName));
                    classifier.buildClassifier(data);
                    stopWatch.stop();

                    stopWatch.start(String.format("%s model evaluation", classifierName));
                    evaluation.evaluateModel(classifier, data);
                    stopWatch.stop();

                    break;
                }

                case CROSS_VALIDATION: {
                    stopWatch.start(String.format("%s model evaluation", classifierName));
                    Classifier classifierCopy = AbstractClassifier.makeCopy(classifier);
                    Random random = new Random(config.getSeed());
                    evaluation.kCrossValidateModel(classifierCopy, data, numFolds, numTests, random);
                    stopWatch.stop();

                    stopWatch.start(String.format("%s model training", classifierName));
                    classifier.buildClassifier(data);
                    stopWatch.stop();

                    break;
                }

                default:
                    throw new IllegalArgumentException("Incorrect evaluation method value!");

            }

            ClassifierDescriptor classifierDescriptor = new ClassifierDescriptor(classifier, evaluation);

            classificationResult.setClassifierDescriptor(classifierDescriptor);
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
