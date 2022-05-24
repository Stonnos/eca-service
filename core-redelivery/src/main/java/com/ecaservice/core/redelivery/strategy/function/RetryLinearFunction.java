package com.ecaservice.core.redelivery.strategy.function;

/**
 * Retry linear function.
 *
 * @author Roman Batygin
 */
public class RetryLinearFunction implements RetryFunction {

    @Override
    public long calculateShiftFactor(long iteration) {
        return iteration;
    }
}
