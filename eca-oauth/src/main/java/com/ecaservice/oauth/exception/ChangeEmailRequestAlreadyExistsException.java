package com.ecaservice.oauth.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;

/**
 * Exception throws in case if active change email request already exists.
 *
 * @author Roman Batygin
 */
public class ChangeEmailRequestAlreadyExistsException extends ValidationErrorException {

    private static final String ERROR_CODE = "ActiveChangeEmailRequest";

    /**
     * Constructor with parameters.
     *
     * @param userId - user id
     */
    public ChangeEmailRequestAlreadyExistsException(Long userId) {
        super(ERROR_CODE, String.format("Active change email request already exists for user [%d]", userId));
    }
}
