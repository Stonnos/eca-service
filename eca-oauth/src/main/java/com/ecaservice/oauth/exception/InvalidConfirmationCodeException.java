package com.ecaservice.oauth.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.oauth.error.EcaOauthErrorCode;

/**
 * Exception throws in case if confirmation code is invalid.
 *
 * @author Roman Batygin
 */
public class InvalidConfirmationCodeException extends ValidationErrorException {

    /**
     * Constructor with parameters.
     */
    public InvalidConfirmationCodeException() {
        super(EcaOauthErrorCode.INVALID_CONFIRMATION_CODE, "Invalid confirmation code");
    }
}
