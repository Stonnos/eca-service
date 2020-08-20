package com.ecaservice.load.test.dto;

import lombok.experimental.UtilityClass;

/**
 * Fields constraints class.
 * @author Roman Batygin
 */
@UtilityClass
public class FieldConstraints {

    public static final int MIN_NUM_REQUESTS = 1;
    public static final int MAX_NUM_REQUESTS = 1000;

    public static final int MIN_NUM_THREADS = 1;
    public static final int MAX_NUM_THREADS = 10;
}
