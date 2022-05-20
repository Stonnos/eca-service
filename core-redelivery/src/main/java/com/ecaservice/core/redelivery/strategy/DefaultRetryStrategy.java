package com.ecaservice.core.redelivery.strategy;

import com.ecaservice.core.redelivery.strategy.function.RetryFunction;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Default retry strategy.
 *
 * @author Roman Batygin
 */
@Data
public class DefaultRetryStrategy implements RetryStrategy {

    private int maxRetries;

    private int maxRetriesInRow;

    private long minRetryIntervalMillis;

    private RetryFunction retryFunction;

    @Override
    public Long calculateNextRetryIntervalMillis(int iteration) {
        if (iteration >= getMaxRetries()) {
            return null;
        }
        if (iteration % getMaxRetriesInRow() != 0) {
            return 0L;
        }
        int nextRetriesRowIdx = iteration / getMaxRetriesInRow() + 1;
        return getMinRetryIntervalMillis() * getRetryFunction().calculateShiftFactor(nextRetriesRowIdx);
    }
}
