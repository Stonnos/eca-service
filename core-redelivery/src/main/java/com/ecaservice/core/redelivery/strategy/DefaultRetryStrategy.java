package com.ecaservice.core.redelivery.strategy;

/**
 * Default retry strategy.
 *
 * @author Roman Batygin
 */
public class DefaultRetryStrategy extends AbstractRetryStrategy {

    @Override
    public long calculateNextRetryIntervalMillis(int iteration) {
        if (iteration >= getMaxRetries()) {
            String errorMessage =
                    String.format("Can't calculate next retry interval: iteration >= maxRetries [%d >= %d]", iteration,
                            getMaxRetries());
            throw new IllegalStateException(errorMessage);
        }
        int nextRetriesRowIdx = iteration / getMaxRetriesInRow();
        long nextRetryInternal =
                getMinRetryIntervalMillis() * getRetryFunction().calculateShiftFactor(nextRetriesRowIdx);
        return Long.min(nextRetryInternal, getMaxRetryIntervalMillis());
    }
}
