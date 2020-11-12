package com.ecaservice.external.api.exception;

/**
 * Invalid url exception.
 *
 * @author Roman Batygin
 */
public class InvalidUrlException extends RuntimeException {

    /**
     * Creates exception object.
     *
     * @param message - error message
     */
    public InvalidUrlException(String message) {
        super(message);
    }
}
