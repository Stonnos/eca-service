package com.ecaservice.server.verifier;

/**
 * Test step verifier.
 *
 * @param <T> - data generic type
 * @author Roman Batygin
 */
@FunctionalInterface
public interface TestStepVerifier<T> {

    /**
     * Verifies test step.
     *
     * @param data - data model
     */
    void verifyStep(T data);
}
