package com.ecaservice.data.loader.dto;

import lombok.experimental.UtilityClass;

/**
 * Field constraints utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class FieldConstraints {

    /**
     * Max length 255
     */
    public static final int MAX_LENGTH_255 = 255;

    /**
     * UUID max size.
     */
    public static final int UUID_MAX_SIZE = 36;

    /**
     * Zero value
     */
    public static final String ZERO_VALUE_STRING = "0";

    /**
     * Max. integer value
     */
    public static final String MAX_INTEGER_VALUE_STRING = "2147483647";
}
