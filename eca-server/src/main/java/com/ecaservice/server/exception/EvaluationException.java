package com.ecaservice.server.exception;

/**
 * Evaluation exception class.
 *
 * @author Roman Batygin
 */
public class EvaluationException extends EcaServiceException {

    /**
     * Creates evaluation exception.
     *
     * @param message - error message
     */
    public EvaluationException(String message) {
        super(message);
    }
}
