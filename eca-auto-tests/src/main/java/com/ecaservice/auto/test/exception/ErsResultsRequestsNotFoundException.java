package com.ecaservice.auto.test.exception;

/**
 * Ers results requests not found exception.
 *
 * @author Roman Batygin
 */
public class ErsResultsRequestsNotFoundException extends RuntimeException {

    /**
     * Creates exception object.
     *
     * @param message - error message
     */
    public ErsResultsRequestsNotFoundException(String message) {
        super(message);
    }
}
