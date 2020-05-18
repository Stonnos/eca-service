package com.ecaservice.notification.util;

import lombok.experimental.UtilityClass;

/**
 * Field constraints utility class.
 */
@UtilityClass
public class FieldConstraints {

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
     * Subject max size
     */
    public static final int SUBJECT_MAX_SIZE = 255;
}
