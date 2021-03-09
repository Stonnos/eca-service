package com.ecaservice.oauth.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;

/**
 * Exception throws in case if active change password request already exists.
 *
 * @author Roman Batygin
 */
public class ChangePasswordRequestAlreadyExistsException extends ValidationErrorException {

    private static final String ERROR_CODE = "ActiveChangePasswordRequest";

    /**
     * Constructor with parameters.
     *
     * @param userId - user id
     */
    public ChangePasswordRequestAlreadyExistsException(Long userId) {
        super(ERROR_CODE, String.format("Active change password request already exists for user [%d]", userId));
    }
}
