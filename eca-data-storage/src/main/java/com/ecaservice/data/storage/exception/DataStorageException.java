package com.ecaservice.data.storage.exception;

/**
 * Data storage exception class.
 *
 * @author Roman Batygin
 */
public class DataStorageException extends RuntimeException {

    /**
     * Creates data storage exception.
     *
     * @param message - error message
     */
    public DataStorageException(String message) {
        super(message);
    }
}
