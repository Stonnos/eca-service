package com.ecaservice.notification.util;

import lombok.experimental.UtilityClass;

/**
 * Field constraints utility class.
 */
@UtilityClass
public class FieldConstraints {

    /**
     * Value 1
     */
    public static final int VALUE_1 = 1;

    /**
     * Email max size
     */
    public static final int EMAIL_MAX_SIZE = 255;

    /**
     * Max length 255
     */
    public static final int MAX_LENGTH_255 = 255;

    /**
     * Email regex
     */
    public static final String EMAIL_REGEX =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * Max. variables size
     */
    public static final int MAX_VARIABLES_SIZE = 50;
}
