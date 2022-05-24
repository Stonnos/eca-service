package com.ecaservice.core.redelivery.strategy.function;

/**
 * Retry constant function.
 *
 * @author Roman Batygin
 */
public class RetryConstantFunction implements RetryFunction {

    private static final long CONSTANT = 1L;

    @Override
    public long calculateShiftFactor(long iteration) {
        return CONSTANT;
    }
}
