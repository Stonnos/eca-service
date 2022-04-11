package com.ecaservice.external.api.dto;

import lombok.experimental.UtilityClass;

/**
 * Constraints utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Constraints {

    /**
     * Min. folds number
     */
    public static final int MIN_FOLDS = 2;

    /**
     * Min tests number
     */
    public static final int MIN_TESTS = 1;

    /**
     * Max folds number
     */
    public static final int MAX_FOLDS = 10;

    /**
     * Max tests number
     */
    public static final int MAX_TESTS = 10;

    /**
     * Min length 1
     */
    public static final int MIN_LENGTH_1 = 1;

    /**
     * Value 0 string
     */
    public static final String VALUE_0_STRING = "0";

    /**
     * Value 1 string
     */
    public static final String VALUE_1_STRING = "1";

    /**
     * Value 2 string
     */
    public static final String VALUE_2_STRING = "2";

    /**
     * Value 100 string
     */
    public static final String VALUE_100_STRING = "100";

    /**
     * Max. integer value
     */
    public static final String MAX_INTEGER_VALUE_STRING = "2147483647";

    /**
     * Max length 255
     */
    public static final int MAX_LENGTH_255 = 255;

    /**
     * UUID max length
     */
    public static final int UUID_MAX_LENGTH = 36;
}
