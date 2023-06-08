package com.ecaservice.oauth.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.oauth.error.EcaOauthErrorCode;

/**
 * Exception throws in case if active reset password request already exists.
 *
 * @author Roman Batygin
 */
public class ResetPasswordRequestAlreadyExistsException extends ValidationErrorException {

    /**
     * Constructor with parameters.
     *
     * @param userId - user id
     */
    public ResetPasswordRequestAlreadyExistsException(Long userId) {
        super(EcaOauthErrorCode.ACTIVE_RESET_PASSWORD_REQUEST,
                String.format("Active reset password request already exists for user [%d]", userId));
    }
}
