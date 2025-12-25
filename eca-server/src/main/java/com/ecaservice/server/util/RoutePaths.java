package com.ecaservice.server.util;

import lombok.experimental.UtilityClass;

/**
 * Route paths.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class RoutePaths {

    /**
     * Evaluation results details path
     */
    public static final String EVALUATION_RESULTS_DETAILS_PATH = "/dashboard/classifiers/evaluation-results/%d";

    /**
     * Experiment details path
     */
    public static final String EXPERIMENT_DETAILS_PATH = "/dashboard/experiments/details/%d";

    /**
     * Experiment results details path
     */
    public static final String EXPERIMENT_RESULTS_DETAILS_PATH = "/dashboard/experiments/results/details/%d";
}
