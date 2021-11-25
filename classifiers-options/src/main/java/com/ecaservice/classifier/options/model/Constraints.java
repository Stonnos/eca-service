package com.ecaservice.classifier.options.model;

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
     * Value 0
     */
    public static final int ZERO_VALUE = 0;

    /**
     * Value 1
     */
    public static final int VALUE_1 = 1;

    /**
     * Value 2
     */
    public static final int VALUE_2 = 2;

    /**
     * Value 10
     */
    public static final int VALUE_10 = 10;

    /**
     * Value 100
     */
    public static final int VALUE_100 = 100;

    /**
     * Decimal value 0.5
     */
    public static final String DECIMAL_VALUE_0_5_STRING = "0.5";

    /**
     * Decimal value 1
     */
    public static final String DECIMAL_VALUE_1_STRING = "1.0";

    /**
     * Decimal value 0
     */
    public static final String DECIMAL_VALUE_0_STRING = "0.0";

    /**
     * Integer max value string
     */
    public static final String INTEGER_MAX_VALUE_STRING = "2147483647";

    /**
     * Hidden layer regex
     */
    public static final String HIDDEN_LAYER_REGEX = "^([0-9],?)+$";

    /**
     * Max individual classifiers
     */
    public static final int MAX_INDIVIDUAL_CLASSIFIERS = 25;
}
