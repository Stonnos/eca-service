package com.ecaservice.core.filter.util;

import com.ecaservice.core.filter.exception.InvalidEnumValueException;
import com.ecaservice.core.filter.exception.InvalidValueFormatException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@Slf4j
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

    /**
     * Parses date into local date object.
     *
     * @param fieldName - field name
     * @param date      - date value
     * @return local date object
     */
    public static LocalDate parseDate(String fieldName, String date) {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ex) {
            log.error("Date time [{}] parse exception for field [{}]: {}", date, fieldName, ex.getMessage());
            String errorMessage =
                    String.format("Invalid date value [%s] for field [%s]. Date must be in format [%s]", date,
                            fieldName, DateTimeFormatter.ISO_LOCAL_DATE.toString());
            throw new InvalidValueFormatException(errorMessage);
        }
    }
}
