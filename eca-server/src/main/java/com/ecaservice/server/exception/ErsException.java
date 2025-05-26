package com.ecaservice.server.exception;

/**
 * ERS exception class.
 *
 * @author Roman Batygin
 */
public class ErsException extends EcaServiceException {

    /**
     * Creates ers exception.
     *
     * @param message - error message
     */
    public ErsException(String message) {
        super(message);
    }
}
