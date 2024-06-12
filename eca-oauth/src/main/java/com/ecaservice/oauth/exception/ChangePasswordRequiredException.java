package com.ecaservice.oauth.exception;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import static com.ecaservice.oauth.security.OAuth2AdditionalErrorCodes.CHANGE_PASSWORD_REQUIRED;

/**
 * Change password required exception.
 *
 * @author Roman Batygin
 */
public class ChangePasswordRequiredException extends OAuth2AuthenticationException {

    /**
     * Change password required exception.
     */
    public ChangePasswordRequiredException() {
        super(CHANGE_PASSWORD_REQUIRED);
    }
}
