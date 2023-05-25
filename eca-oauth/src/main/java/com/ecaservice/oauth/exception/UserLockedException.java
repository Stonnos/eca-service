package com.ecaservice.oauth.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.oauth.error.EcaOauthErrorCode;

/**
 * Exception throws in case if user was locked.
 *
 * @author Roman Batygin
 */
public class UserLockedException extends ValidationErrorException {

    /**
     * Constructor with parameters.
     *
     * @param userId - user id
     */
    public UserLockedException(Long userId) {
        super(EcaOauthErrorCode.USER_LOCKED, String.format("User was locked [%d]", userId));
    }
}
