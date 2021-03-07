package com.ecaservice.oauth.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;

/**
 * Exception throws in case if password is invalid.
 *
 * @author Roman Batygin
 */
public class InvalidPasswordException extends ValidationErrorException {

    private static final String ERROR_CODE = "InvalidPassword";

    /**
     * Constructor with parameters.
     */
    public InvalidPasswordException() {
        super(ERROR_CODE, "Invalid password");
    }
}
