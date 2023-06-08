package com.ecaservice.oauth.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.oauth.error.EcaOauthErrorCode;

/**
 * Exception throws in case if token is invalid.
 *
 * @author Roman Batygin
 */
public class InvalidTokenException extends ValidationErrorException {

    /**
     * Constructor with parameters.
     */
    public InvalidTokenException() {
        super(EcaOauthErrorCode.INVALID_TOKEN, "Invalid token");
    }
}
