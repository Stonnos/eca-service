package com.ecaservice.oauth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * Two factor authentication required exception.
 *
 * @author Roman Batygin
 */
public class TfaRequiredException extends OAuth2Exception {

    private static final String TOKEN = "token";
    private static final String EXPIRES_IN = "expires_in";

    /**
     * Creates tfa exception.
     *
     * @param expiresIn - tfa code expires in seconds
     */
    public TfaRequiredException(String token, long expiresIn) {
        super("Two-factor authentication required");
        this.addAdditionalInformation(TOKEN, token);
        this.addAdditionalInformation(EXPIRES_IN, String.valueOf(expiresIn));
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
