package com.ecaservice.common.web.exception;

import lombok.Getter;

/**
 * Exception throws in case of validation errors.
 *
 * @author Roman Batygin
 */
public class ValidationErrorException extends RuntimeException {

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
    public ValidationErrorException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
