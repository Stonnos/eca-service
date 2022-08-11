package com.ecaservice.oauth.service.mail.dictionary;

import lombok.experimental.UtilityClass;

/**
 * Email message template variables.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TemplateVariablesDictionary {

    /**
     * User name variable
     */
    public static final String USERNAME_KEY = "userName";

    /**
     * Password variable
     */
    public static final String PASSWORD_KEY = "password";

    /**
     * Reset password url variable
     */
    public static final String RESET_PASSWORD_URL_KEY = "resetPasswordUrl";

    /**
     * Token validity in minutes variable
     */
    public static final String VALIDITY_MINUTES_KEY = "validityMinutes";

    /**
     * Token validity in hours variable
     */
    public static final String VALIDITY_HOURS_KEY = "validityHours";

    /**
     * Two factor authentication code
     */
    public static final String TFA_CODE = "tfaCode";

    /**
     * Change password url variable
     */
    public static final String CHANGE_PASSWORD_URL_KEY = "changePasswordUrl";

    /**
     * Change email url variable
     */
    public static final String CHANGE_EMAIL_URL_KEY = "changeEmailUrl";

    /**
     * New email
     */
    public static final String NEW_EMAIL = "newEmail";
}
