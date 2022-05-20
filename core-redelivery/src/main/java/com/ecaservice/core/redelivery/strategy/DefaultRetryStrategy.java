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

    private long maxRetries;

    private long maxRetriesInRow;

    private long minRetryIntervalMillis;

    private RetryFunction retryFunction;

    @Override
    public LocalDateTime getNextRetryDate(long iteration) {
        if (iteration >= getMaxRetries()) {
            return null;
        }
        if (iteration % getMaxRetriesInRow() != 0) {
            return LocalDateTime.now();
        }
        long nextRetriesRowIdx = iteration / getMaxRetriesInRow() + 1L;
        long nextRetryIntervalMillis =
                getMinRetryIntervalMillis() * retryFunction.calculateShiftFactor(nextRetriesRowIdx);
        return LocalDateTime.now().plus(nextRetryIntervalMillis, ChronoUnit.MILLIS);
    }
}
