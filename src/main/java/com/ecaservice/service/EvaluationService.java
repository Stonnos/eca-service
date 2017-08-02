package com.ecaservice.service;

import com.ecaservice.model.ClassificationResult;
import com.ecaservice.model.EvaluationMethod;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

/**
 * Implements classifier model evaluation.
 * @author Roman Batygin
 */
public interface EvaluationService {

    /**
     * Evaluates classifier model.
     * @param classifier classifier object
     * @param data input data
     * @param evaluationMethod evaluation method
     * @param numFolds the number of folds for k * V cross - validation method
     * @param numTests the number of tests for k * V cross - validation method
     * @return <tt>ClassificationResult</tt> object
     */
    ClassificationResult evaluateModel(AbstractClassifier classifier, Instances data,
                                       EvaluationMethod evaluationMethod, Integer numFolds, Integer numTests);

}
