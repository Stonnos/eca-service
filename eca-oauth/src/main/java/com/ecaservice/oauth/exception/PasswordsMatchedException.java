package com.ecaservice.oauth.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.oauth.error.EcaOauthErrorCode;

/**
 * Exception throws in case if old and new passwords matches
 *
 * @author Roman Batygin
 */
public class PasswordsMatchedException extends ValidationErrorException {

    /**
     * Constructor with parameters.
     */
    public PasswordsMatchedException() {
        super(EcaOauthErrorCode.PASSWORD_MATCHED, "Old and new passwords matched");
    }
}
