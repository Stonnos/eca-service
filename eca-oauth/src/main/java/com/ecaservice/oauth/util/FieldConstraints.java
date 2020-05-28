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
     * Login regex
     */
    public static final String LOGIN_REGEX = "^[a-z0-9]+$";

    /**
     * Login name min length
     */
    public static final int LOGIN_MIN_LENGTH = 3;

    /**
     * Login name max length
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
     * First name regex
     */
    public static final String FIRST_NAME_REGEX = "^([A-Z][a-z]+)|([А-Я][а-я]+)$";

    /**
     * First name max size
     */
    public static final int FIRST_NAME_MAX_SIZE = 30;
}
