package com.ecaservice.server.exception;

/**
 * Evaluation timeout exception class.
 *
 * @author Roman Batygin
 */
public class EvaluationTimeoutException extends EcaServiceException {

    /**
     * Creates evaluation timeout exception.
     *
     * @param message - error message
     */
    public EvaluationTimeoutException(String message) {
        super(message);
    }
}
