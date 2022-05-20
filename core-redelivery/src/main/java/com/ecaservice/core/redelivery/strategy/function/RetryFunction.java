package com.ecaservice.core.redelivery.strategy.function;

/**
 * Retry function interface.
 *
 * @author Roman Batygin
 */
public interface RetryFunction {

    /**
     * Calculates shift factor depending on iteration number.
     *
     * @param iteration - iteration (retry number)
     * @return shift factor
     */
    long calculateShiftFactor(long iteration);
}
