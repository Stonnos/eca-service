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
     *
     * @param userId - user id
     */
    public PasswordsMatchedException(Long userId) {
        super(EcaOauthErrorCode.PASSWORD_MATCHED,
                String.format("Old and new passwords matched for user [%d]", userId));
    }
}
