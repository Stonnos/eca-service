package com.ecaservice.oauth.exception;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

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
        super("change_password_required");
    }
}
