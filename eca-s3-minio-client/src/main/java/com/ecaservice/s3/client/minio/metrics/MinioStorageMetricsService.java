package com.ecaservice.s3.client.minio.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.ecaservice.s3.client.minio.metrics.MetricConstants.OBJECT_SIZE_BYTES_METRIC;
import static com.ecaservice.s3.client.minio.metrics.MetricConstants.REQUEST_ERROR_METRIC;
import static com.ecaservice.s3.client.minio.metrics.MetricConstants.REQUEST_SUCCESS_METRIC;

/**
 * Minio storage metrics service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioStorageMetricsService {

    private static final String METHOD_TAG = "method";

    private final MeterRegistry meterRegistry;

    /**
     * Tracks request success.
     *
     * @param method - request method
     */
    public void trackRequestSuccess(String method) {
        var counter = meterRegistry.counter(REQUEST_SUCCESS_METRIC, METHOD_TAG, method);
        counter.increment();
    }

    /**
     * Tracks request error.
     *
     * @param method - request method
     */
    public void trackRequestError(String method) {
        var counter = meterRegistry.counter(REQUEST_ERROR_METRIC, METHOD_TAG, method);
        counter.increment();
    }

    /**
     * Tracks object size bytes.
     *
     * @param size - object size in bytes
     */
    public void trackObjectSizeBytes(long size) {
        var summary = meterRegistry.summary(OBJECT_SIZE_BYTES_METRIC);
        summary.record(size);
    }
}
