package com.ecaservice.util;

import com.ecaservice.model.InputData;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.evaluation.EvaluationOption;
import eca.ensemble.AbstractHeterogeneousClassifier;
import eca.ensemble.StackingClassifier;
import org.springframework.util.Assert;
import weka.classifiers.AbstractClassifier;

import java.util.Map;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
public class Utils {

    /**
     * Validates classifier input data.
     *
     * @param inputData            - input data
     * @param evaluationMethod     - evaluation method
     * @param evaluationOptionsMap - evaluation options map
     */
    public static void validateInputData(InputData inputData, EvaluationMethod evaluationMethod,
                                         Map<EvaluationOption, String> evaluationOptionsMap) {
        Assert.notNull(inputData, "Input data is not specified!");
        Assert.notNull(inputData.getClassifier(), "Classifier is not specified!");
        Assert.notNull(inputData.getData(), "Input data is not specified!");
        Assert.notNull(evaluationMethod, "Evaluation method is not specified!");
        Assert.notNull(evaluationOptionsMap, "Evaluation options map is not specified!");
    }

    /**
     * Checks if classifier is heterogeneous ensemble.
     *
     * @param classifier - classifier
     * @return {@code true} if classifier is heterogeneous ensemble
     */
    public static boolean isHeterogeneousEnsembleClassifier(AbstractClassifier classifier) {
        return classifier instanceof AbstractHeterogeneousClassifier || classifier instanceof StackingClassifier;
    }
}
