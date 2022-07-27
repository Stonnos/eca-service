package com.ecaservice.external.api.test.util;

import lombok.experimental.UtilityClass;

/**
 * Constraints utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Constraints {

    /**
     * Min. threads number
     */
    public static final int MIN_NUM_THREADS = 1;

    /**
     * Max. threads number
     */
    public static final int MAX_NUM_THREADS = 10;

    /**
     * Precision value
     */
    public static final int PRECISION = 19;

    /**
     * Scale value
     */
    public static final int SCALE = 4;

    /**
     * Classifier download url max length
     */
    public static final int CLASSIFIER_DOWNLOAD_URL_MAX_LENGTH = 1024;
}
