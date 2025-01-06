package com.ecaservice.core.transactional.outbox.error;

/**
 * Exception strategy interface.
 *
 * @author Roman Batygin
 */
public interface ExceptionStrategy {

    /**
     * Checks that request with specified exception must be repeated.
     *
     * @param ex - exception object
     * @return {@code true} or {@code false}
     */
    boolean notFatal(Exception ex);
}
