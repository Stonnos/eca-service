package com.ecaservice.oauth.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.oauth.validation.annotations.UniqueEmail;

/**
 * Exception throws in case if duplication email.
 *
 * @author Roman Batygin
 */
public class EmailDuplicationException extends ValidationErrorException {

    private static final String ERROR_CODE = UniqueEmail.class.getSimpleName();

    /**
     * Constructor with parameters.
     *
     * @param userId - user id
     */
    public EmailDuplicationException(Long userId) {
        super(ERROR_CODE, String.format("Can't set email for user [%d], because its exists", userId));
    }
}
