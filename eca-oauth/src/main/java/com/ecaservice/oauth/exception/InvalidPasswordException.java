package com.ecaservice.oauth.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.oauth.error.EcaOauthErrorCode;

/**
 * Exception throws in case if password is invalid.
 *
 * @author Roman Batygin
 */
public class InvalidPasswordException extends ValidationErrorException {

    /**
     * Constructor with parameters.
     */
    public InvalidPasswordException() {
        super(EcaOauthErrorCode.INVALID_PASSWORD, "Invalid password");
    }
}
