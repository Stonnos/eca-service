package com.ecaservice.oauth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * Two factor authentication required exception.
 *
 * @author Roman Batygin
 */
public class TfaRequiredException extends OAuth2Exception {

    public TfaRequiredException() {
        super("Two-factor authentication required");
    }

    @Override
    public String getOAuth2ErrorCode() {
        return "tfa_required";
    }

    @Override
    public int getHttpErrorCode() {
        return HttpStatus.FORBIDDEN.value();
    }
}
