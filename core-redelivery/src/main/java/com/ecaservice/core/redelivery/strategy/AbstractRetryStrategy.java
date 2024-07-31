package com.ecaservice.core.redelivery.strategy;

import com.ecaservice.core.redelivery.strategy.function.RetryFunction;
import lombok.Getter;
import lombok.Setter;

/**
 * Retry strategy abstract class.
 *
 * @author Roman Batygin
 */
@Setter
@Getter
public abstract class AbstractRetryStrategy {

    /**
     * Max retries
     */
    private int maxRetries;

    /**
     * Max retries in row
     */
    private int maxRetriesInRow;

    /**
     * Min. retry interval in millis
     */
    private long minRetryIntervalMillis;

    /**
     * Max retry interval in millis
     */
    private long maxRetryIntervalMillis;

    /**
     * Retry function
     */
    private RetryFunction retryFunction;

    /**
     * Gets next retry interval millis.
     *
     * @param iteration - iteration (retry number)
     * @return next retry interval millis
     */
    public abstract long calculateNextRetryIntervalMillis(int iteration);
}
