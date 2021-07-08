package com.ecaservice.core.lock.service;

/**
 * Callback interface.
 *
 * @author Roman Batygin
 */
@FunctionalInterface
public interface Callback {

    /**
     * Performs callback action.
     */
    void apply();
}
