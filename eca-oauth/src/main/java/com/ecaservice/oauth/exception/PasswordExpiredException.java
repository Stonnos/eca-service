package com.ecaservice.oauth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * Password expired exception.
 *
 * @author Roman Batygin
 */
public class PasswordExpiredException extends OAuth2Exception {

    /**
     * Creates password expired exception.
     */
    public PasswordExpiredException() {
        super("Password has been expired");
    }

    @Override
    public String getOAuth2ErrorCode() {
        return "password_expired";
    }

    @Override
    public int getHttpErrorCode() {
        return HttpStatus.FORBIDDEN.value();
    }
}
