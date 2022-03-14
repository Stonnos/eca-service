package com.ecaservice.auto.test.exception;

/**
 * Experiment results not found exception.
 *
 * @author Roman Batygin
 */
public class ExperimentResultsNotFoundException extends RuntimeException {

    /**
     * Creates exception object.
     *
     * @param message - error message
     */
    public ExperimentResultsNotFoundException(String message) {
        super(message);
    }
}
