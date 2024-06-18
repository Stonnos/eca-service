package com.ecaservice.oauth.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.oauth.error.EcaOauthErrorCode;

/**
 * Exception throws in case if active change email request already exists.
 *
 * @author Roman Batygin
 */
public class ChangeEmailRequestAlreadyExistsException extends ValidationErrorException {

    /**
     * Constructor with parameters.
     */
    public ChangeEmailRequestAlreadyExistsException() {
        super(EcaOauthErrorCode.ACTIVE_CHANGE_EMAIL_REQUEST, "Active change email request already exists");
    }
}
