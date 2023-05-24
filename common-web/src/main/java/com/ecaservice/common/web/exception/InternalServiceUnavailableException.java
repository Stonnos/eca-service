package com.ecaservice.common.web.exception;

/**
 * Internal service unavailable exception.
 *
 * @author Roman Batygin
 */
public class InternalServiceUnavailableException extends RuntimeException {

    /**
     * Creates exception object.
     *
     * @param message - error message
     */
    public InternalServiceUnavailableException(String message) {
        super(message);
    }
}
