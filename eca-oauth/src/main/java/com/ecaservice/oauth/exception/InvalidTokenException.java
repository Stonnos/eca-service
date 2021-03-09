package com.ecaservice.oauth.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;

/**
 * Exception throws in case if token is invalid.
 *
 * @author Roman Batygin
 */
public class InvalidTokenException extends ValidationErrorException {

    private static final String ERROR_CODE = "InvalidToken";

    /**
     * Constructor with parameters.
     *
     * @param token - token value
     */
    public InvalidTokenException(String token) {
        super(ERROR_CODE, String.format("Invalid token [%s]", token));
    }
}
