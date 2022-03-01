package com.ecaservice.core.lock.fallback;

/**
 * Fallback interface in case if lock can't be acquire.
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
