package com.ecaservice.external.api.metrics;

import lombok.experimental.UtilityClass;

/**
 * Metrics constants.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class MetricConstants {

    static final String REQUEST_STATUS_TAG = "request-status";
    static final String BASE_METRIC_PREFIX = "eca-external-api";
    static final String REQUEST_DURATION_METRIC = BASE_METRIC_PREFIX + ".request.duration";
    static final String REQUESTS_TOTAL_METRIC = BASE_METRIC_PREFIX + ".requests.total";
    static final String RESPONSES_TOTAL_METRIC = BASE_METRIC_PREFIX + ".responses.total";
    static final String REQUESTS_METRIC = BASE_METRIC_PREFIX + ".requests";
}
