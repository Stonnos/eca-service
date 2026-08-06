package com.ecaservice.oauth.exception;

import com.ecaservice.oauth.security.model.SetPasswordRequiredError;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

/**
 * Change password required exception.
 *
 * @author Roman Batygin
 */
public class ChangePasswordRequiredException extends OAuth2AuthenticationException {

    /**
     * Change password required exception.
     *
     * @param error - error code
     */
    public ChangePasswordRequiredException(SetPasswordRequiredError error) {
        super(error);
    }
}
