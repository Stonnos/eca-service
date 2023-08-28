package com.ecaservice.data.loader.util;

import lombok.experimental.UtilityClass;

/**
 * Field constraints utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class FieldConstraints {

    /**
     * Value 1
     */
    public static final int VALUE_1 = 1;

    /**
     * Value 2
     */
    public static final int VALUE_2 = 2;

    /**
     * Max length 255
     */
    public static final int MAX_LENGTH_255 = 255;

    /**
     * Date time pattern
     */
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * Local date time max length
     */
    public static final int LOCAL_DATE_TIME_MAX_LENGTH = 19;

    /**
     * UUID max size.
     */
    public static final int UUID_MAX_SIZE = 36;

    /**
     * UUID pattern.
     */
    public static final String UUID_PATTERN =
            "^[0-9a-f]{8}-[0-9a-f]{4}-[34][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";

    /**
     * Zero value
     */
    public static final String ZERO_VALUE_STRING = "0";

    /**
     * Min. integer value
     */
    public static final String MIN_INTEGER_VALUE_STRING = "-2147483648";

    /**
     * Max. integer value
     */
    public static final String MAX_INTEGER_VALUE_STRING = "2147483647";
}
