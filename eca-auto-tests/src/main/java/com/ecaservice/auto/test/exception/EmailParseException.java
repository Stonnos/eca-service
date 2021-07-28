package com.ecaservice.auto.test.exception;

/**
 * Email parsing error exception.
 *
 * @author Roman Batygin
 */
public class EmailParseException extends RuntimeException {

    /**
     * Creates exception object.
     *
     * @param message - error message
     */
    public EmailParseException(String message) {
        super(message);
    }
}
