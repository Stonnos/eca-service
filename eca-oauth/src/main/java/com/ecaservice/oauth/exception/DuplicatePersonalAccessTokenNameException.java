package com.ecaservice.oauth.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.oauth.error.EcaOauthErrorCode;

/**
 * Exception throws in case if personal access token name already exists.
 *
 * @author Roman Batygin
 */
public class DuplicatePersonalAccessTokenNameException extends ValidationErrorException {

    /**
     * Constructor with parameters.
     */
    public DuplicatePersonalAccessTokenNameException() {
        super(EcaOauthErrorCode.DUPLICATE_PERSONAL_ACCESS_TOKEN_NAME, "Duplicate personal access token name");
    }
}
