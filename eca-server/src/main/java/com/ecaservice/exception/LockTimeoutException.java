package com.ecaservice.exception;

/**
 * Lock timeout exception
 */
public class LockTimeoutException extends RuntimeException {

    public LockTimeoutException(String message) {
        super(message);
    }
}
