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
     * Reset password token validity in minutes variable
     */
    public static final String VALIDITY_MINUTES_KEY = "validityMinutes";

    /**
     * Two factor authentication code
     */
    public static final String TFA_CODE = "tfaCode";
}
