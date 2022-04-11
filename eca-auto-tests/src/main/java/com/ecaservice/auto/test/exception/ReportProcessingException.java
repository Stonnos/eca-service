package com.ecaservice.auto.test.exception;

/**
 * Report processing exception.
 *
 * @author Roman Batygin
 */
public class ReportProcessingException extends RuntimeException {

    /**
     * Creates exception object.
     *
     * @param message - error message
     */
    public ReportProcessingException(String message) {
        super(message);
    }
}
