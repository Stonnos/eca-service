package com.ecaservice.core.redelivery.strategy;

import com.ecaservice.core.redelivery.strategy.function.RetryFunction;
import lombok.Getter;
import lombok.Setter;

/**
 * Default retry strategy.
 *
 * @author Roman Batygin
 */
public class DefaultRetryStrategy implements RetryStrategy {

    @Setter
    @Getter
    private int maxRetries;
    @Setter
    @Getter
    private int maxRetriesInRow;
    @Setter
    @Getter
    private long minRetryIntervalMillis;
    @Setter
    @Getter
    private RetryFunction retryFunction;

    @Override
    public long calculateNextRetryIntervalMillis(int iteration) {
        if (iteration >= getMaxRetries()) {
            String errorMessage =
                    String.format("Can't calculate next retry interval: iteration >= maxRetries [%d >= %d]", iteration,
                            getMaxRetries());
            throw new IllegalStateException(errorMessage);
        }
        int nextRetriesRowIdx = iteration / maxRetriesInRow;
        return minRetryIntervalMillis * retryFunction.calculateShiftFactor(nextRetriesRowIdx);
    }
}
