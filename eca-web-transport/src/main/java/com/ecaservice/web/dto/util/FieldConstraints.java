package com.ecaservice.web.dto.util;

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
    public static final int CONFIGURATION_NAME_MAX_LENGTH = 64;

    /**
     * Date time pattern
     */
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
}
