package com.ecaservice.oauth.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.oauth.validation.annotations.UniqueEmail;

/**
 * Exception throws in case if email is already bound to account.
 *
 * @author Roman Batygin
 */
public class EmailAlreadyBoundException extends ValidationErrorException {

    private static final String ERROR_CODE = "EmailAlreadyBound";

    /**
     * Constructor with parameters.
     *
     * @param userId - user id
     */
    public EmailAlreadyBoundException(Long userId) {
        super(ERROR_CODE, String.format("Email is already bound to account for user [%d]", userId));
    }
}
