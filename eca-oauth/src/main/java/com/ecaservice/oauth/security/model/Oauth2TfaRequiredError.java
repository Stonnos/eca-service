package com.ecaservice.oauth.security.model;

import lombok.Getter;
import org.springframework.security.oauth2.core.OAuth2Error;

/**
 * Oauth2 tfa required error.
 *
 * @author Roman Batygin
 */
public class Oauth2TfaRequiredError extends OAuth2Error {

    /**
     * Temporary token value for tfa code
     */
    @Getter
    private final String token;

    /**
     * Token expiration time seconds
     */
    @Getter
    private final long expiresIn;

    /**
     * Creates oauth2 tfa required error.
     *
     * @param token     - token value
     * @param expiresIn - token expiration time seconds
     */

    public Oauth2TfaRequiredError(String token, long expiresIn) {
        super("tfa_required", "Two-factor authentication required", null);
        this.token = token;
        this.expiresIn = expiresIn;
    }
}
