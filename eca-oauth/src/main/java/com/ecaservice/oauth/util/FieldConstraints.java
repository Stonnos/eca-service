package com.ecaservice.oauth.util;

import lombok.experimental.UtilityClass;

/**
 * Field constraints utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class FieldConstraints {

    /**
     * Configuration name max length
     */
    public static final int LOGIN_MAX_LENGTH = 32;

    /**
     * Email max size
     */
    public static final int EMAIL_MAX_SIZE = 255;

    /**
     * Email regex
     */
    public static final String EMAIL_REGEX =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * First name max size
     */
    public static final int FIRST_NAME_MAX_SIZE = 30;
}
