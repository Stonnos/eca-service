package com.ecaservice.common.web.exception;

/**
 * Invalid operation exception.
 *
 * @author Roman Batygin
 */
public class InvalidOperationException extends ValidationErrorException {

    public static final String ERROR_CODE = "InvalidOperation";

    /**
     * Creates exception object.
     *
     * @param message - error message
     */
    public InvalidOperationException(String message) {
        super(ERROR_CODE, message);
    }
}
