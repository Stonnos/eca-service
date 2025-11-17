package com.ecaservice.server.metrics;

import lombok.experimental.UtilityClass;

/**
 * Metrics constants.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class MetricConstants {

    public static final String BASE_METRIC_PREFIX = "eca-server";

    public static final String EVALUATION_MQ_REQUEST_ERROR_METRIC = BASE_METRIC_PREFIX + ".evaluation.mq.request.error";

    public static final String EVALUATION_REQUEST_TOTAL_METRIC = BASE_METRIC_PREFIX + ".evaluation.request.total";

    public static final String EXPERIMENT_REQUEST_TOTAL_METRIC = BASE_METRIC_PREFIX + ".experiment.request.total";

    public static final String EVALUATION_REQUEST_STATUS_TOTAL_METRIC = BASE_METRIC_PREFIX
            + ".evaluation.request.status.total";

    public static final String EXPERIMENT_REQUEST_STATUS_TOTAL_METRIC = BASE_METRIC_PREFIX +
            ".experiment.request.status.total";
}
