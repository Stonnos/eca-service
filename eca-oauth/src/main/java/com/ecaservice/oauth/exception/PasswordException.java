package com.ecaservice.oauth.exception;

import lombok.Getter;

/**
 * Exception throws in case of password operations errors.
 *
 * @author Roman Batygin
 */
public class PasswordException extends RuntimeException {

    /**
     * Error code.
     */
    @Getter
    private final String errorCode;

    /**
     * Creates exception object.
     *
     * @param errorCode - error code
     * @param message   - error message
     */
    public PasswordException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
