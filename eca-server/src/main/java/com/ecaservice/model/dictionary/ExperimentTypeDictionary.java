package com.ecaservice.model.dictionary;

import lombok.experimental.UtilityClass;

/**
 * Experiment type dictionary.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ExperimentTypeDictionary {

    public static final String NEURAL_NETWORKS_NAME = "Нейронные сети";
    public static final String HETEROGENEOUS_ENSEMBLE_NAME = "Неоднородный ансамбль";
    public static final String MODIFIED_HETEROGENEOUS_ENSEMBLE_NAME = "Мод. неоднородный ансамбль";
    public static final String ADA_BOOST_NAME = "Алгоритм AdaBoost";
    public static final String STACKING_NAME = "Алгоритм Stacking";
    public static final String STACKING_CV_NAME = "Алгоритм Stacking CV";
    public static final String KNN_NAME = "Алгоритм KNN";
    public static final String RANDOM_FORESTS_NAME = "Случайные леса";
    public static final String DECISION_TREE_NAME = "Деревья решений";
}
