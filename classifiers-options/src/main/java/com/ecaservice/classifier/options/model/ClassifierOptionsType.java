package com.ecaservice.classifier.options.model;

import lombok.experimental.UtilityClass;

/**
 * Classifier options type for JSON inheritance.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ClassifierOptionsType {

    public static final String DECISION_TREE = "decision_tree";
    public static final String LOGISTIC = "logistic";
    public static final String KNN = "knn";
    public static final String NEURAL_NETWORK = "neural_network";
    public static final String J48 = "j48";
    public static final String STACKING = "stacking";
    public static final String RANDOM_NETWORKS = "random_networks";
    public static final String EXTRA_TREES = "extra_trees";
    public static final String ADA_BOOST = "ada_boost";
    public static final String HEC = "heterogeneous_classifier";
}
