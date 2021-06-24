package com.ecaservice.core.filter.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    private static final String POINT_SEPARATOR = ".";

    /**
     * Splits string by "." separator.
     *
     * @param str - string value
     * @return tokens array
     */
    public static String[] splitByPointSeparator(String str) {
        return StringUtils.split(str, POINT_SEPARATOR);
    }
}
