package com.ecaservice.service;

import com.ecaservice.model.ClassificationResult;
import com.ecaservice.model.entity.EvaluationMethod;
import com.ecaservice.model.InputData;

/**
 * Implements classifier model evaluation.
 *
 * @author Roman Batygin
 */
public interface EvaluationService {

    /**
     * Evaluates classifier model.
     *
     * @param inputData {@link InputData} object
     * @param evaluationMethod evaluation method
     * @param numFolds         the number of folds for k * V cross - validation method
     * @param numTests         the number of tests for k * V cross - validation method
     * @return <tt>ClassificationResult</tt> object
     */
    ClassificationResult evaluateModel(InputData inputData, EvaluationMethod evaluationMethod,
                                       Integer numFolds, Integer numTests);

}
