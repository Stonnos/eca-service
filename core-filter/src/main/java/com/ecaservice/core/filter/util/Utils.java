package com.ecaservice.core.filter.util;

import com.ecaservice.core.filter.exception.InvalidEnumValueException;
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

    /**
     * Converts enum value to enum object.
     *
     * @param enumType - enum class
     * @param name     - enum value
     * @param <T>      - enum generic type
     * @return enum object
     */
    public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name) {
        try {
            return Enum.valueOf(enumType, name);
        } catch (IllegalArgumentException ex) {
            throw new InvalidEnumValueException(name, enumType);
        }
    }
}
