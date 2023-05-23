package com.ecaservice.server.exception;

/**
 * Data storage exception class.
 *
 * @author Roman Batygin
 */
public class DataStorageException extends EcaServiceException {

    /**
     * Creates data storage exception.
     *
     * @param message - error message
     */
    public DataStorageException(String message) {
        super(message);
    }
}
