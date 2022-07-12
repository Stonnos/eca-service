package com.ecaservice.core.redelivery.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service to manage with retry metrics.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RetryMeterService {

    private static final String RETRY_REQUEST_TYPE_TAG = "request_type";

    private static final String BASE_METRIC_PREFIX = "core";
    private static final String RETRY_REQUEST_CACHE_SIZE_METRIC = BASE_METRIC_PREFIX + ".retry.request.cache.size";
    private static final String FAILED_RETRIES_METRIC = BASE_METRIC_PREFIX + ".retries.failed.total";
    private static final String SUCCESS_RETRIES_METRIC = BASE_METRIC_PREFIX + ".retries.success.total";
    private static final String EXHAUSTED_RETRIES_METRIC = BASE_METRIC_PREFIX + ".retries.exhausted.total";
    private static final String ERROR_RETRIES_METRIC = BASE_METRIC_PREFIX + ".retries.error.total";

    private final MeterRegistry meterRegistry;

    /**
     * Tracks retry request cache size.
     *
     * @param requestType - request type
     */
    public void trackRetryRequestCacheSize(String requestType) {
        var counter = meterRegistry.counter(RETRY_REQUEST_CACHE_SIZE_METRIC, RETRY_REQUEST_TYPE_TAG, requestType);
        counter.increment();
    }

    /**
     * Tracks retry success.
     *
     * @param requestType - request type
     */
    public void trackRetrySuccess(String requestType) {
        var counter = meterRegistry.counter(SUCCESS_RETRIES_METRIC, RETRY_REQUEST_TYPE_TAG, requestType);
        counter.increment();
    }

    /**
     * Tracks retry failed.
     *
     * @param requestType - request type
     */
    public void trackRetryFailed(String requestType) {
        var counter = meterRegistry.counter(FAILED_RETRIES_METRIC, RETRY_REQUEST_TYPE_TAG, requestType);
        counter.increment();
    }

    /**
     * Tracks retry exhausted.
     *
     * @param requestType - request type
     */
    public void trackRetryExhausted(String requestType) {
        var counter = meterRegistry.counter(EXHAUSTED_RETRIES_METRIC, RETRY_REQUEST_TYPE_TAG, requestType);
        counter.increment();
    }

    /**
     * Tracks retry error.
     *
     * @param requestType - request type
     */
    public void trackRetryError(String requestType) {
        var counter = meterRegistry.counter(ERROR_RETRIES_METRIC, RETRY_REQUEST_TYPE_TAG, requestType);
        counter.increment();
    }
}
