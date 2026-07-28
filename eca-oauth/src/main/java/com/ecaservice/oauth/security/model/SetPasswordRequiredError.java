package com.ecaservice.oauth.security.model;

import lombok.Getter;
import org.springframework.security.oauth2.core.OAuth2Error;

import static com.ecaservice.oauth.security.OAuth2AdditionalErrorCodes.CHANGE_PASSWORD_REQUIRED;

/**
 * Set password required error.
 *
 * @author Roman Batygin
 */
public class SetPasswordRequiredError extends OAuth2Error {

    /**
     * Temporary token value
     */
    @Getter
    private final String token;

    /**
     * Creates set password required error.
     *
     * @param token - token value
     */
    public SetPasswordRequiredError(String token) {
        super(CHANGE_PASSWORD_REQUIRED, "Set password required", null);
        this.token = token;
    }
}
