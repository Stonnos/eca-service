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
     * Max string length 255
     */
    public static final int MAX_LENGTH_255 = 255;

    /**
     * Filters list max length
     */
    public static final int FILTERS_LIST_MAX_LENGTH = 50;

    /**
     * Values list max length
     */
    public static final int VALUES_LIST_MAX_LENGTH = 50;

    /**
     * Date time pattern
     */
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
}
