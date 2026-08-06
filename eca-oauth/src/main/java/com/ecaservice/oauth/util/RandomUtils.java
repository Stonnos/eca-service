package com.ecaservice.oauth.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Random string utils.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class RandomUtils {

    /**
     * Generates random string with specified length.
     *
     * @param length - string length
     * @return random string
     */
    public static String randomString(int length) {
        return RandomStringUtils.random(length, false, true);
    }
}
