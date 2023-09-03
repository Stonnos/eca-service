package com.ecaservice.external.api.config.metrics;

import lombok.experimental.UtilityClass;

/**
 * Metrics constants.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class MetricConstants {

    public static final String RESPONSE_CODE_TAG = "response-code";
    public static final String BASE_METRIC_PREFIX = "eca-external-api";
    public static final String REQUEST_DURATION_METRIC = BASE_METRIC_PREFIX + ".request.duration";
    public static final String REQUESTS_TOTAL_METRIC = BASE_METRIC_PREFIX + ".requests.total";
    public static final String RESPONSES_TOTAL_METRIC = BASE_METRIC_PREFIX + ".responses.total";
    public static final String RESPONSES_METRIC = BASE_METRIC_PREFIX + ".response";
}
