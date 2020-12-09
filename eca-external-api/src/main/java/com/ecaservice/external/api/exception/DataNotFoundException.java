package com.ecaservice.external.api.exception;

/**
 * Data not found exception.
 *
 * @author Roman Batygin
 */
public class DataNotFoundException extends RuntimeException {

    /**
     * Creates exception object.
     *
     * @param message - error message
     */
    public DataNotFoundException(String message) {
        super(message);
    }
}
