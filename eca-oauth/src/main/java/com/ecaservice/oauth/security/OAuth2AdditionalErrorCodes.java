package com.ecaservice.oauth.security;

import lombok.experimental.UtilityClass;

/**
 * Oauth2 additional error codes.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class OAuth2AdditionalErrorCodes {

    /**
     * Invalid tfa code
     */
    public static final String INVALID_TFA_CODE = "invalid_code";

    /**
     * Tfa code expired
     */
    public static final String TFA_CODE_EXPIRED = "tfa_code_expired";

    /**
     * Change password required
     */
    public static final String CHANGE_PASSWORD_REQUIRED = "change_password_required";
}
