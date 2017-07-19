package com.ecaservice.service;

import com.ecaservice.model.ClassificationResult;
import com.ecaservice.model.EvaluationMethod;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

/**
 * @author Roman Batygin
 */
public interface EcaService {

    ClassificationResult execute(AbstractClassifier classifier, Instances data,
                                 EvaluationMethod evaluationMethod, Integer numFolds, Integer numTests);
}
