package com.ecaservice.server.model;

/**
 * Interface for cancelable operation.
 *
 * @author Roman Batygin
 */
@FunctionalInterface
public interface Cancelable {

    /**
     * Is operation cancelled?
     *
     * @return {@code true} if operation has been cancelled, otherwise {@code false}
     */
    boolean isCancelled();
}
