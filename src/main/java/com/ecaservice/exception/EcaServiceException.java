package com.ecaservice.exception;

/**
 * Eca - service base exception class.
 *
 * @author Roman Batygin
 */

public class EcaServiceException extends RuntimeException {

    /**
     * Creates <tt>EcaServiceException</tt> object
     *
     * @param message error message
     */
    public EcaServiceException(String message) {
        super(message);
    }
}
