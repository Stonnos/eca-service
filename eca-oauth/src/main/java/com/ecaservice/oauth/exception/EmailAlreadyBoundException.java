package com.ecaservice.oauth.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.oauth.error.EcaOauthErrorCode;

/**
 * Exception throws in case if email is already bound to account.
 *
 * @author Roman Batygin
 */
public class EmailAlreadyBoundException extends ValidationErrorException {

    /**
     * Constructor with parameters.
     *
     * @param userId - user id
     */
    public EmailAlreadyBoundException(Long userId) {
        super(EcaOauthErrorCode.EMAIL_ALREADY_BOUND,
                String.format("Email is already bound to account for user [%d]", userId));
    }
}
