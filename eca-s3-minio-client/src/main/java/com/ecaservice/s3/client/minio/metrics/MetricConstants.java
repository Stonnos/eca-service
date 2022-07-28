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
    public static final String UPLOAD_OBJECT_METRIC = BASE_METRIC_PREFIX + "object.upload";
    public static final String DOWNLOAD_OBJECT_METRIC = BASE_METRIC_PREFIX + "object.download";
    public static final String REMOVE_OBJECT_METRIC = BASE_METRIC_PREFIX + "object.remove";
    public static final String GET_PRESIGNED_URL_METRIC = BASE_METRIC_PREFIX + "object.presigned-url";
    public static final String REQUEST_SUCCESS_METRIC = BASE_METRIC_PREFIX + ".request.success.total";
    public static final String REQUEST_ERROR_METRIC = BASE_METRIC_PREFIX + ".request.error.total";
    public static final String OBJECT_SIZE_BYTES_METRIC = BASE_METRIC_PREFIX + ".object.size.bytes";
}
