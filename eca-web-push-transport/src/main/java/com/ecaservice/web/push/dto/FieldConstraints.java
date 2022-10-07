package com.ecaservice.web.push.dto;

import lombok.experimental.UtilityClass;

/**
 * Field constraints utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class FieldConstraints {

    /**
     * Max string length 255
     */
    public static final int MAX_LENGTH_255 = 255;

    /**
     * UUID max length
     */
    public static final int UUID_MAX_LENGTH = 36;

    /**
     * Value 1
     */
    public static final int VALUE_1 = 1;

    /**
     * UUID pattern.
     */
    public static final String UUID_PATTERN =
            "^[0-9a-f]{8}-[0-9a-f]{4}-[34][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";

    /**
     * Max. additional properties size
     */
    public static final int MAX_ADDITIONAL_PROPERTIES_SIZE = 50;

    /**
     * Receivers max size
     */
    public static final int RECEIVERS_MAX_SIZE = 1000;
}
