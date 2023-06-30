package com.ecaservice.classifier.options.mapping;

import lombok.experimental.UtilityClass;

/**
 * Bean ordered class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Ordered {

    public static final int DECISION_TREE_ORDER = 0;
    public static final int LOGISTIC_ORDER = 1;
    public static final int KNN_ORDER = 2;
    public static final int NEURAL_NETWORK_ORDER = 3;
    public static final int J48_ORDER = 4;
    public static final int STACKING_ORDER = 5;
    public static final int RANDOM_NETWORKS_ORDER = 6;
    public static final int EXTRA_TREES_ORDER = 7;
    public static final int RANDOM_FORESTS_ORDER = 8;
    public static final int ADA_BOOST_ORDER = 9;
    public static final int HEC_ORDER = 10;
}
