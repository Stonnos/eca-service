package com.ecaservice.core.redelivery.strategy.function;

import lombok.Getter;
import lombok.Setter;

/**
 * Retry degree function (y = a^x).
 *
 * @author Roman Batygin
 */
public class RetryDegreeFunction implements RetryFunction {

    private static final double DEFAULT_DEGREE = 2.0d;

    @Setter
    @Getter
    private double degree = DEFAULT_DEGREE;

    @Override
    public long calculateShiftFactor(long iteration) {
        return (long) Math.pow(degree, iteration);
    }
}
