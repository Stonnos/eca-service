package com.ecaservice.service;


import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.model.ClassificationResult;
import com.ecaservice.model.EvaluationMethod;
import eca.beans.ClassifierDescriptor;
import eca.core.evaluation.Evaluation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.Arrays;
import java.util.Random;

/**
 * @author Roman Batygin
 */
@Slf4j
@Service
public class EvaluationServiceImpl implements EvaluationService {

    private static final int MINIMUM_NUMBER_OF_FOLDS = 2;

    private static final int MINIMUM_NUMBER_OF_TESTS = 1;

    private CrossValidationConfig crossValidationConfig;

    @Autowired
    public EvaluationServiceImpl(CrossValidationConfig crossValidationConfig) {
        this.crossValidationConfig = crossValidationConfig;
    }

    @Override
    public ClassificationResult evaluateModel(AbstractClassifier classifier,
                                              Instances data,
                                              EvaluationMethod evaluationMethod,
                                              Integer numFolds,
                                              Integer numTests) {

        Assert.notNull(classifier,"Classifier is not specified!");
        Assert.notNull(data,"Input data is not specified!");
        Assert.notNull(evaluationMethod, "Evaluation method is not specified!");

        if (numFolds == null) {
            numFolds = crossValidationConfig.getNumFolds();
        }

        Assert.state(numFolds >= MINIMUM_NUMBER_OF_FOLDS,
                String.format("Number of folds must be greater than %d!", MINIMUM_NUMBER_OF_FOLDS));

        if (numTests == null) {
            numTests = crossValidationConfig.getNumTests();
        }

        Assert.state(numTests >= MINIMUM_NUMBER_OF_TESTS,
                String.format("Number of tests must be greater than %d!", MINIMUM_NUMBER_OF_TESTS));

        log.info("Starting model evaluation.");

        log.trace("Model evaluation starting for classifier = {}, options = {}, data = {}, evaluationMethod = {}",
                classifier.getClass().getSimpleName(), Arrays.asList(classifier.getOptions()),
                data.relationName(), evaluationMethod);

        ClassificationResult classificationResult = new ClassificationResult();

        try {
            Evaluation evaluation = new Evaluation(data);

            StopWatch stopWatch = new StopWatch();

            switch (evaluationMethod) {

                case TRAINING_DATA: {
                    stopWatch.start("Model training");
                    classifier.buildClassifier(data);
                    stopWatch.stop();

                    stopWatch.start("Model evaluation");
                    evaluation.evaluateModel(classifier, data);
                    stopWatch.stop();

                    break;
                }

                case CROSS_VALIDATION: {
                    stopWatch.start("Model evaluation");

                    Classifier classifierCopy = AbstractClassifier.makeCopy(classifier);
                    Random random = new Random(crossValidationConfig.getSeed());
                    evaluation.kCrossValidateModel(classifierCopy, data, numFolds, numTests, random);

                    stopWatch.stop();

                    stopWatch.start("Model training");
                    classifier.buildClassifier(data);
                    stopWatch.stop();

                    break;
                }

            }

            ClassifierDescriptor classifierDescriptor = new ClassifierDescriptor(classifier, evaluation);

            classificationResult.setClassifierDescriptor(classifierDescriptor);
            classificationResult.setSuccess(true);

            log.info("Evaluation for model '{}' has been successfully finished!",
                    classifier.getClass().getSimpleName());
            log.info(stopWatch.prettyPrint());

        } catch (Exception exception) {
            log.error("evaluateModel: ", exception);
            classificationResult.setErrorMessage(exception.getMessage());
        }

        return classificationResult;
    }

}
