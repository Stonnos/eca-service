package com.ecaservice.external.api.dto;

/**
 * Experiment type.
 *
 * @author Roman Batygin
 */
public enum ExApiExperimentType {

    /**
     * Optimal options automatic selection for neural networks.
     */
    NEURAL_NETWORKS,

    /**
     * Optimal options automatic selection for heterogeneous ensemble algorithm.
     */
    HETEROGENEOUS_ENSEMBLE,

    /**
     * Optimal options automatic selection for modified heterogeneous ensemble algorithm.
     */
    MODIFIED_HETEROGENEOUS_ENSEMBLE,

    /**
     * Optimal options automatic selection for AdaBoost algorithm.
     */
    ADA_BOOST,

    /**
     * Optimal options automatic selection for stacking algorithm.
     */
    STACKING,

    /**
     * Optimal options automatic selection for k - nearest neighbours algorithm.
     */
    KNN,

    /**
     * Optimal options automatic selection Random forests algorithm.
     */
    RANDOM_FORESTS,

    /**
     * Optimal options automatic selection for stacking algorithm using cross - validation method for
     * creation meta data set.
     */
    STACKING_CV,

    /**
     * Optimal options automatic selection decision tree algorithms.
     */
    DECISION_TREE
}
