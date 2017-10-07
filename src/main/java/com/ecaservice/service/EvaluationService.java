package com.ecaservice.service;

import com.ecaservice.model.ClassificationResult;
import com.ecaservice.model.EvaluationMethod;
import com.ecaservice.model.EvaluationOption;
import com.ecaservice.model.InputData;

import java.util.Map;

/**
 * Implements classifier model evaluation.
 *
 * @author Roman Batygin
 */
public interface EvaluationService {

    /**
     * Evaluates classifier model.
     *
     * @param inputData        {@link InputData} object
     * @param evaluationMethod evaluation method
     * @param evaluationOptionsMap evaluation options map
     * @return {@link ClassificationResult} object
     */
    ClassificationResult evaluateModel(InputData inputData, EvaluationMethod evaluationMethod,
                                       Map<EvaluationOption, String> evaluationOptionsMap);

}
