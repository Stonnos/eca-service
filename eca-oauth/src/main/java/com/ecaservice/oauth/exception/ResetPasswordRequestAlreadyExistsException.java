package com.ecaservice.oauth.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;

/**
 * Exception throws in case if active reset password request already exists.
 *
 * @author Roman Batygin
 */
public class ResetPasswordRequestAlreadyExistsException extends ValidationErrorException {

    private static final String ERROR_CODE = "ActiveResetPasswordRequest";

    /**
     * Constructor with parameters.
     *
     * @param userId - user id
     */
    public ResetPasswordRequestAlreadyExistsException(Long userId) {
        super(ERROR_CODE, String.format("Active reset password request already exists for user [%d]", userId));
    }
}
