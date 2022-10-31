package com.ecaservice.base.model.util;

import lombok.experimental.UtilityClass;

/**
 * Dto field constraints utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class FieldConstraints {

    /**
     * Email max size
     */
    public static final int EMAIL_MAX_SIZE = 50;

    /**
     * Email regex
     */
    public static final String EMAIL_REGEX =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
}
