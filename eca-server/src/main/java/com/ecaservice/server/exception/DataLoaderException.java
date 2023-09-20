package com.ecaservice.server.exception;

/**
 * Data loader exception class.
 *
 * @author Roman Batygin
 */
public class DataLoaderException extends EcaServiceException {

    /**
     * Creates data storage exception.
     *
     * @param message - error message
     */
    public DataLoaderException(String message) {
        super(message);
    }
}
