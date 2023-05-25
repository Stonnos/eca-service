package com.ecaservice.oauth.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.oauth.error.EcaOauthErrorCode;

/**
 * Exception throws in case if duplication email.
 *
 * @author Roman Batygin
 */
public class EmailDuplicationException extends ValidationErrorException {

    /**
     * Constructor with parameters.
     *
     * @param userId - user id
     */
    public EmailDuplicationException(Long userId) {
        super(EcaOauthErrorCode.EMAIL_DUPLICATION,
                String.format("Can't set email for user [%d], because its exists", userId));
    }
}
