package com.ecaservice.ers.exception;

/**
 * Data not found exception class.
 *
 * @author Roman Batygin
 */
public class DataNotFoundException extends EvaluationResultsException {

    /**
     * Creates data not found exception.
     *
     * @param message - error message
     */
    public DataNotFoundException(String message) {
        super(message);
    }
}
