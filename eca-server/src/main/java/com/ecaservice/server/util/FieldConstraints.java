package com.ecaservice.server.util;

import lombok.experimental.UtilityClass;

/**
 * Dto field constraints utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class FieldConstraints {

    /**
     * Precision length
     */
    public static final int PRECISION = 19;

    /**
     * Scale length
     */
    public static final int SCALE = 4;

    /**
     * Experiment download url max length
     */
    public static final int EXPERIMENT_DOWNLOAD_URL_MAX_LENGTH = 1024;

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
}
