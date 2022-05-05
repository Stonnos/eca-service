package com.ecaservice.core.lock.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service to manage with lock metrics.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LockMeterService {

    private static final String LOCK_NAME_TAG = "lockName";

    private static final String BASE_METRIC_PREFIX = "core";
    private static final String SUCCESS_LOCK_METRIC = BASE_METRIC_PREFIX + ".lock.success.total";
    private static final String SUCCESS_UNLOCK_METRIC = BASE_METRIC_PREFIX + ".unlock.success.total";
    private static final String ERROR_UNLOCK_METRIC = BASE_METRIC_PREFIX + ".unlock.error.total";
    private static final String FAILED_LOCK_METRIC = BASE_METRIC_PREFIX + ".lock.failed.total";
    private static final String ACQUIRE_LOCK_ERROR_METRIC = BASE_METRIC_PREFIX + ".lock.acquire.error.total";

    private final MeterRegistry meterRegistry;

    /**
     * Tracks success lock.
     *
     * @param lockName - lock name
     */
    public void trackSuccessLock(String lockName) {
        var counter = meterRegistry.counter(SUCCESS_LOCK_METRIC, LOCK_NAME_TAG, lockName);
        counter.increment();
    }

    /**
     * Tracks failed lock.
     *
     * @param lockName - lock name
     */
    public void trackFailedLock(String lockName) {
        var counter = meterRegistry.counter(FAILED_LOCK_METRIC, LOCK_NAME_TAG, lockName);
        counter.increment();
    }

    /**
     * Tracks lock acquire error.
     *
     * @param lockName - lock name
     */
    public void trackAcquireLockError(String lockName) {
        var counter = meterRegistry.counter(ACQUIRE_LOCK_ERROR_METRIC, LOCK_NAME_TAG, lockName);
        counter.increment();
    }

    /**
     * Tracks success unlock.
     *
     * @param lockName - lock name
     */
    public void trackSuccessUnlock(String lockName) {
        var counter = meterRegistry.counter(SUCCESS_UNLOCK_METRIC, LOCK_NAME_TAG, lockName);
        counter.increment();
    }

    /**
     * Tracks unlock error.
     *
     * @param lockName - lock name
     */
    public void trackUnlockError(String lockName) {
        var counter = meterRegistry.counter(ERROR_UNLOCK_METRIC, LOCK_NAME_TAG, lockName);
        counter.increment();
    }
}
