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
     */
    public EmailDuplicationException() {
        super(EcaOauthErrorCode.EMAIL_DUPLICATION, "Can't set email for account, because its exists");
    }
}
