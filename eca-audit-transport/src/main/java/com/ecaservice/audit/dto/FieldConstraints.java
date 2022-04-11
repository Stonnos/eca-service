package com.ecaservice.audit.dto;

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
}
