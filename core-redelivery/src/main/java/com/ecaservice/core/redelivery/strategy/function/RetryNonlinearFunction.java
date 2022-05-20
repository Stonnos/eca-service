package com.ecaservice.core.redelivery.strategy.function;

/**
 * Retry nonlinear function (y = 2^x).
 *
 * @author Roman Batygin
 */
public class RetryNonlinearFunction implements RetryFunction {

    private static final double TWO = 2.0d;

    @Override
    public long calculateShiftFactor(long iteration) {
        return (long) Math.pow(TWO, iteration);
    }
}
