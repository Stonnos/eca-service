package com.ecaservice.external.api.exception;

/**
 * Process file exception.
 *
 * @author Roman Batygin
 */
public class ProcessFileException extends RuntimeException {

    /**
     * Creates exception object.
     *
     * @param message - error message
     */
    public ProcessFileException(String message) {
        super(message);
    }
}
