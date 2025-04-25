package com.ecaservice.server.exception;

/**
 * Evaluation canceled exception class.
 *
 * @author Roman Batygin
 */
public class EvaluationCanceledException extends EcaServiceException {

    /**
     * Creates evaluation canceled exception.
     *
     * @param message - error message
     */
    public EvaluationCanceledException(String message) {
        super(message);
    }
}
