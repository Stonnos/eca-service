package com.ecaservice.oauth.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;

/**
 * Exception throws in case if user was locked.
 *
 * @author Roman Batygin
 */
public class UserLockedException extends ValidationErrorException {

    private static final String ERROR_CODE = "UserLocked";

    /**
     * Constructor with parameters.
     *
     * @param userId - user id
     */
    public UserLockedException(Long userId) {
        super(ERROR_CODE, String.format("User was locked [%d]", userId));
    }
}
