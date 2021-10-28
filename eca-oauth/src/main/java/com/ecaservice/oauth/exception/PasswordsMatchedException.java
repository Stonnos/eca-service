package com.ecaservice.oauth.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;

/**
 * Exception throws in case if old and new passwords matches
 *
 * @author Roman Batygin
 */
public class PasswordsMatchedException extends ValidationErrorException {

    private static final String ERROR_CODE = "PasswordsMatched";

    /**
     * Constructor with parameters.
     *
     * @param userId - user id
     */
    public PasswordsMatchedException(Long userId) {
        super(ERROR_CODE, String.format("Old and new passwords matched for user [%d]", userId));
    }
}
