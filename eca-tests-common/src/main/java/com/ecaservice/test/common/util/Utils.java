package com.ecaservice.test.common.util;

import com.ecaservice.test.common.model.TestResult;
import com.ecaservice.test.common.service.TestResultsMatcher;
import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
     * Copy resource into byte array.
     *
     * @param resource - resource object
     * @return byte array
     * @throws IOException in case of I/O error
     */
    public static byte[] copyToByteArray(Resource resource) throws IOException {
        @Cleanup InputStream inputStream = resource.getInputStream();
        @Cleanup ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOUtils.copy(inputStream, outputStream);
        return outputStream.toByteArray();
    }

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

    /**
     * Calculates test result.
     *
     * @param matcher - matcher object
     * @return test result
     */
    public static TestResult calculateTestResult(TestResultsMatcher matcher) {
        if (matcher.getTotalNotMatched() == 0 && matcher.getTotalNotFound() == 0) {
            return TestResult.PASSED;
        }
        return TestResult.FAILED;
    }
}
