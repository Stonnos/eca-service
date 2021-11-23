package com.ecaservice.ers.dto;

import lombok.experimental.UtilityClass;

/**
 * Constraints utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Constraints {

    /**
     * Max. string length - 255.
     */
    public static final int MAX_LENGTH_255 = 255;

    /**
     * Decimal min zero value.
     */
    public static final String DECIMAL_MIN_ZERO = "0.0";

    /**
     * Decimal max one value.
     */
    public static final String DECIMAL_MAX_ONE = "1.0";

    /**
     * Decimal max value - 100.
     */
    public static final String DECIMAL_MAX_100 = "100.0";

    /**
     * Min zero value.
     */
    public static final int MIN_ZERO = 0;

    /**
     * Min value - 2.
     */
    public static final int MIN_2 = 2;

    /**
     * Min value - 1.
     */
    public static final int MIN_1 = 1;

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
     * Optimal classifier options reports max items
     */
    public static final int OPTIMAL_CLASSIFIER_OPTIONS_REPORT_MAX_ITEMS = 5;
}
