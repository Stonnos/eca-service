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
        if (iteration % getMaxRetriesInRow() != 0) {
            return 0L;
        }
        int nextRetriesRowIdx = iteration / getMaxRetriesInRow() + 1;
        return getMinRetryIntervalMillis() * retryFunction.calculateShiftFactor(nextRetriesRowIdx);
    }
}
