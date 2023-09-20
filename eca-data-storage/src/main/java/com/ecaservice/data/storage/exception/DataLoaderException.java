package com.ecaservice.data.storage.exception;

/**
 * Data loader exception class.
 *
 * @author Roman Batygin
 */
public class DataLoaderException extends RuntimeException {

    /**
     * Creates data storage exception.
     *
     * @param message - error message
     */
    public DataLoaderException(String message) {
        super(message);
    }
}
