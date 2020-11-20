package com.ecaservice.external.api.dto;

import lombok.experimental.UtilityClass;

/**
 * Constraints utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Constraints {

    public static final int MIN_FOLDS = 2;
    public static final int MIN_TESTS = 1;
    public static final int MAX_FOLDS = 10;
    public static final int MAX_TESTS = 10;
}
