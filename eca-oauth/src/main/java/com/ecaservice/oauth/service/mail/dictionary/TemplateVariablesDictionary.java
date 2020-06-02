package com.ecaservice.oauth.service.mail.dictionary;

import lombok.experimental.UtilityClass;

/**
 * Email message template variables.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TemplateVariablesDictionary {

    public static final String USERNAME_KEY = "userName";
    public static final String PASSWORD_KEY = "password";
    public static final String RESET_PASSWORD_URL_KEY = "resetPasswordUrl";
    public static final String VALIDITY_MINUTES_KEY = "validityMinutes";
}
