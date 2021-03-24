package com.ecaservice.ers.exception;

/**
 * Evaluation results basic exception.
 *
 * @author Roman Batygin
 */
public class EvaluationResultsException extends RuntimeException {

    /**
     * Creates evaluation results exception.
     *
     * @param message - error message
     */
    public EvaluationResultsException(String message) {
        super(message);
    }
}
