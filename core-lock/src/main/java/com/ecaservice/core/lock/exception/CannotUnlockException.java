package com.ecaservice.core.lock.exception;

/**
 * Can't unlock exception class.
 *
 * @author Roman Batygin
 */
public class CannotUnlockException extends RuntimeException {

    /**
     * Constructor with parameters.
     *
     * @param message - error message
     */
    public CannotUnlockException(String message) {
        super(message);
    }
}
