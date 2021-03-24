package com.ecaservice.ers.config;

import lombok.experimental.UtilityClass;

/**
 * Metrics utility class for constants.
 */
@UtilityClass
public class MetricConstants {

    private static final String BASE_METRIC_PREFIX = "ers";
    private static final String OPERATION_METRIC_PREFIX = BASE_METRIC_PREFIX + ".operation";
    private static final String TIMED_METRIC = ".timed";

    public static final String SAVE_EVALUATION_RESULTS_TIMED_METRIC_NAME =
            OPERATION_METRIC_PREFIX + ".save-evaluation-results" + TIMED_METRIC;
    public static final String GET_EVALUATION_RESULTS_TIMED_METRIC_NAME =
            OPERATION_METRIC_PREFIX + ".get-evaluation-results" + TIMED_METRIC;
    public static final String GET_OPTIMAL_CLASSIFIER_OPTIONS_TIMED_METRIC_NAME =
            OPERATION_METRIC_PREFIX + ".get-optimal-classifier-options" + TIMED_METRIC;
}
