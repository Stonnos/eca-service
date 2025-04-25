package com.ecaservice.server.model;

/**
 * Interface for cancelable task.
 *
 * @author Roman Batygin
 */
public interface CancelableTask {

    /**
     * Is operation cancelled?
     *
     * @return {@code true} if operation has been cancelled, otherwise {@code false}
     */
    boolean isCancelled();

    /**
     * Cancels task.
     */
    void cancel();
}
