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
     * Max string length 1024
     */
    public static final int MAX_LENGTH_1024 = 1024;

    /**
     * UUID max length
     */
    public static final int UUID_MAX_LENGTH = 36;

    /**
     * Local date time max length
     */
    public static final int LOCAL_DATE_TIME_MAX_LENGTH = 19;

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

    /**
     * Max. page size
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * Max. long value
     */
    public static final String MAX_LONG_VALUE_STRING = "9223372036854775807";

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

    /**
     * Minimum folds number
     */
    public static final String MINIMUM_NUM_FOLDS_STRING = "2";

    /**
     * Maximum folds number
     */
    public static final String MAXIMUM_NUM_FOLDS_STRING = "10";

    /**
     * Minimum tests number
     */
    public static final String MINIMUM_NUM_TESTS_STRING = "1";

    /**
     * Maximum tests number
     */
    public static final String MAXIMUM_NUM_TESTS_STRING = "10";

    /**
     * Evaluation total time max length
     */
    public static final int EVALUATION_TOTAL_TIME_MAX_LENGTH = 11;

    /**
     * Min num classes
     */
    public static final String MIN_NUM_CLASSES_STRING = "2";

    /**
     * Value 100
     */
    public static final String VALUE_100_STRING = "100";

    /**
     * Value 1
     */
    public static final String VALUE_1_STRING = "1";

    /**
     * Value 2
     */
    public static final String VALUE_2_STRING = "2";

    /**
     * Estimated time left max length
     */
    public static final int ESTIMATED_TIME_LEFT_MAX_LENGTH = 8;

    /**
     * Value 1
     */
    public static final int VALUE_1 = 1;

    /**
     * Value 0
     */
    public static final int VALUE_0 = 0;

    /**
     * Optimal classifier options reports max items
     */
    public static final int OPTIMAL_CLASSIFIER_OPTIONS_RESPONSES_MAX_ITEMS = 5;

    /**
     * Experiment results max items
     */
    public static final int EXPERIMENT_RESULTS_MAX_ITEMS = 5;

    /**
     * UUID pattern.
     */
    public static final String UUID_PATTERN =
            "^[0-9a-f]{8}-[0-9a-f]{4}-[34][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";

    /**
     * Max. additional properties size
     */
    public static final int MAX_ADDITIONAL_PROPERTIES_SIZE = 50;
}
