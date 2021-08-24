package com.ecaservice.test.common.util;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    private static final String GMT_TIME_ZONE = "GMT";

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss:SSSS");

    /**
     * Gets total time between two dates in format HH:mm:ss:SS.
     *
     * @param start - start date
     * @param end   - end date
     * @return total time string
     */
    public static String totalTime(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null) {
            long totalTimeMillis = ChronoUnit.MILLIS.between(start, end);
            LocalDateTime totalTime =
                    Instant.ofEpochMilli(totalTimeMillis).atZone(ZoneId.of(GMT_TIME_ZONE)).toLocalDateTime();
            return TIME_FORMATTER.format(totalTime);
        }
        return null;
    }
}
