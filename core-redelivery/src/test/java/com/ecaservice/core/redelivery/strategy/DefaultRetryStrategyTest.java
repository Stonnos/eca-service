package com.ecaservice.core.redelivery.strategy;

import com.ecaservice.core.redelivery.strategy.function.RetryNonlinearFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link DefaultRetryStrategy} class.
 *
 * @author Roman Batygin
 */
class DefaultRetryStrategyTest {

    private static final int MAX_RETRIES = 100;
    private static final int MAX_RETRIES_IN_ROW = 10;
    private static final long MIN_RETRY_INTERVAL_MILLIS = 30000L;

    private DefaultRetryStrategy retryStrategy;

    @BeforeEach
    void init() {
        retryStrategy = new DefaultRetryStrategy();
        retryStrategy.setMaxRetries(MAX_RETRIES);
        retryStrategy.setMaxRetriesInRow(MAX_RETRIES_IN_ROW);
        retryStrategy.setMinRetryIntervalMillis(MIN_RETRY_INTERVAL_MILLIS);
        retryStrategy.setRetryFunction(new RetryNonlinearFunction());
    }

    @Test
    void testCalculateNextRetryIntervalMillis() {
        for (int i = 0; i < MAX_RETRIES; i++) {
            long nextRetryIntervalMillis = retryStrategy.calculateNextRetryIntervalMillis(i);
            if (i % MAX_RETRIES_IN_ROW != 0) {
                assertThat(nextRetryIntervalMillis).isEqualTo(MIN_RETRY_INTERVAL_MILLIS);
            } else {
                int iteration = i / MAX_RETRIES_IN_ROW + 1;
                long expected =
                        MIN_RETRY_INTERVAL_MILLIS * retryStrategy.getRetryFunction().calculateShiftFactor(iteration);
                assertThat(nextRetryIntervalMillis).isEqualTo(expected);
            }
        }
    }

    @Test
    void testCalculateNextRetryIntervalMillisShouldThrowIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> retryStrategy.calculateNextRetryIntervalMillis(MAX_RETRIES));
    }
}
