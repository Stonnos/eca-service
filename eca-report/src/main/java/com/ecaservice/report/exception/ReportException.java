package com.ecaservice.report.exception;

/**
 * Report exception class.
 *
 * @author Roman Batygin
 */
public class ReportException extends RuntimeException {

    /**
     * Creates report exception.
     *
     * @param message - message string
     */
    public ReportException(String message) {
        super(message);
    }
}
