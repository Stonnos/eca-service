package com.ecaservice.oauth.service.mail.dictionary;

import lombok.experimental.UtilityClass;

/**
 * Email template codes.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Templates {

    /**
     * Tfa code template
     */
    public static final String TFA_CODE = "TFA_CODE";

    /**
     * Reset password template
     */
    public static final String RESET_PASSWORD = "RESET_PASSWORD";

    /**
     * Change password template
     */
    public static final String CHANGE_PASSWORD = "CHANGE_PASSWORD";

    /**
     * New user template
     */
    public static final String NEW_USER = "NEW_USER";

    /**
     * Change email request template
     */
    public static final String CHANGE_EMAIL = "CHANGE_EMAIL";

    /**
     * Email changed template
     */
    public static final String EMAIL_CHANGED = "EMAIL_CHANGED";

    /**
     * Password changed template
     */
    public static final String PASSWORD_CHANGED = "PASSWORD_CHANGED";

    /**
     * Password reset template
     */
    public static final String PASSWORD_RESET = "PASSWORD_RESET";

    /**
     * User locked template
     */
    public static final String USER_LOCKED = "USER_LOCKED";

    /**
     * User unlocked template
     */
    public static final String USER_UNLOCKED = "USER_UNLOCKED";
}
