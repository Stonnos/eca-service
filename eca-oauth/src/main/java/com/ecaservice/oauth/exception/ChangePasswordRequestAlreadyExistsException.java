package com.ecaservice.oauth.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.oauth.error.EcaOauthErrorCode;

/**
 * Exception throws in case if active change password request already exists.
 *
 * @author Roman Batygin
 */
public class ChangePasswordRequestAlreadyExistsException extends ValidationErrorException {

    /**
     * Constructor with parameters.
     *
     * @param userId - user id
     */
    public ChangePasswordRequestAlreadyExistsException(Long userId) {
        super(EcaOauthErrorCode.ACTIVE_CHANGE_PASSWORD_REQUEST,
                String.format("Active change password request already exists for user [%d]", userId));
    }
}
