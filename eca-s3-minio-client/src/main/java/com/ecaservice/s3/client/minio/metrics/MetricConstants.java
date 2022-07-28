package com.ecaservice.s3.client.minio.metrics;

import lombok.experimental.UtilityClass;

/**
 * Metrics constants.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class MetricConstants {

    public static final String BASE_METRIC_PREFIX = "s3-storage";
    public static final String OBJECT_REQUEST_METRIC = BASE_METRIC_PREFIX + ".object.request";
    public static final String REQUEST_SUCCESS_METRIC = BASE_METRIC_PREFIX + ".request.success.total";
    public static final String REQUEST_ERROR_METRIC = BASE_METRIC_PREFIX + ".request.error.total";
    public static final String OBJECT_SIZE_BYTES_METRIC = BASE_METRIC_PREFIX + ".object.size.bytes";
}
