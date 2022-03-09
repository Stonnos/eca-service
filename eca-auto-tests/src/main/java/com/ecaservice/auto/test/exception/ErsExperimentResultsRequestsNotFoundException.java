package com.ecaservice.auto.test.exception;

/**
 * Ers experiment results requests not found exception.
 *
 * @author Roman Batygin
 */
public class ErsExperimentResultsRequestsNotFoundException extends RuntimeException {

    /**
     * Creates exception object.
     *
     * @param message - error message
     */
    public ErsExperimentResultsRequestsNotFoundException(String message) {
        super(message);
    }
}
