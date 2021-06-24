package com.ecaservice.report.data.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/**
 * Range utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class RangeUtils {

    private static final String DATE_RANGE_LOWER_PREFIX = "с ";
    private static final String DATE_RANGE_UPPER_PREFIX = "по ";

    /**
     * Format string dates values as range.
     *
     * @param lowerBound - date lower bound
     * @param upperBound - date upper bound
     * @return dates range as string
     */
    public static String formatDateRange(String lowerBound, String upperBound) {
        StringBuilder stringBuilder = new StringBuilder();
        if (StringUtils.isNotBlank(lowerBound)) {
            stringBuilder.append(DATE_RANGE_LOWER_PREFIX).append(lowerBound);
        }
        if (StringUtils.isNotBlank(upperBound)) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(StringUtils.SPACE);
            }
            stringBuilder.append(DATE_RANGE_UPPER_PREFIX).append(upperBound);
        }
        return stringBuilder.toString();
    }
}
