package com.ecaservice.core.lock.fallback;

/**
 * Fallback interface.
 *
 * @author Roman Batygin
 */
@FunctionalInterface
public interface FallbackHandler {

    /**
     * Performs fallback action.
     *
     * @param lockKey lock key
     */
    void fallback(String lockKey);
}
