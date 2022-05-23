package com.ecaservice.core.redelivery.strategy;

import com.ecaservice.core.redelivery.strategy.function.RetryFunction;
import lombok.Data;

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
    public long calculateNextRetryIntervalMillis(int iteration) {
        if (iteration >= getMaxRetries()) {
            String errorMessage =
                    String.format("Can't calculate next retry interval: iteration >= maxRetries [%d >= %d]", iteration,
                            getMaxRetries());
            throw new IllegalStateException(errorMessage);
        }
        int nextRetriesRowIdx = iteration / maxRetriesInRow + 1;
        return minRetryIntervalMillis * retryFunction.calculateShiftFactor(nextRetriesRowIdx);
    }
}
