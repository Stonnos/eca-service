package com.ecaservice.oauth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * Change password required exception.
 *
 * @author Roman Batygin
 */
public class ChangePasswordRequiredException extends OAuth2Exception {

    /**
     * Change password required exception.
     */
    public ChangePasswordRequiredException() {
        super("Password must be changed");
    }

    @Override
    public String getOAuth2ErrorCode() {
        return "change_password_required";
    }

    @Override
    public int getHttpErrorCode() {
        return HttpStatus.FORBIDDEN.value();
    }
}
