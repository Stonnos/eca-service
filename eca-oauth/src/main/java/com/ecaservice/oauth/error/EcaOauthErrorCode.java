package com.ecaservice.oauth.error;

import com.ecaservice.common.error.model.ErrorDetails;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Error code.
 *
 * @author Roman Batygin
 */
@Getter
@RequiredArgsConstructor
public enum EcaOauthErrorCode implements ErrorDetails {

    /**
     * Active change email request already exists code
     */
    ACTIVE_CHANGE_EMAIL_REQUEST("ActiveChangeEmailRequest"),

    /**
     * Active change password request already exists code
     */
    ACTIVE_CHANGE_PASSWORD_REQUEST("ActiveChangePasswordRequest"),

    /**
     * EMail already bounded for current user
     */
    EMAIL_ALREADY_BOUND("EmailAlreadyBound"),

    /**
     * Email duplication error code
     */
    EMAIL_DUPLICATION("UniqueEmail"),

    /**
     * Invalid password code
     */
    INVALID_PASSWORD("InvalidPassword"),

    /**
     * Invalid token code
     */
    INVALID_TOKEN("InvalidToken"),

    /**
     * Old and new password matched for change password request
     */
    PASSWORD_MATCHED("PasswordsMatched"),

    /**
     * Active reset password request already exists code
     */
    ACTIVE_RESET_PASSWORD_REQUEST("ActiveResetPasswordRequest"),

    /**
     * User locked error code
     */
    USER_LOCKED("UserLocked");

    /**
     * Error code
     */
    private final String code;
}
