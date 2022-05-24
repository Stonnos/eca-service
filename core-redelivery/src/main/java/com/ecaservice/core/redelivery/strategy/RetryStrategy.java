package com.ecaservice.core.redelivery.strategy;

/**
 * Retry strategy interface.
 *
 * @author Roman Batygin
 */
public interface RetryStrategy {

    /**
     * Gets max retries.
     *
     * @return max retries
     */
    int getMaxRetries();

    /**
     * Gets max retries in row.
     *
     * @return max retries in row
     */
    int getMaxRetriesInRow();

    /**
     * Gets min retry interval in millis.
     *
     * @return min retry interval in millis
     */
    long getMinRetryIntervalMillis();

    /**
     * Gets next retry interval millis.
     *
     * @param iteration - iteration (retry number)
     * @return next retry interval millis
     */
    long calculateNextRetryIntervalMillis(int iteration);
}
