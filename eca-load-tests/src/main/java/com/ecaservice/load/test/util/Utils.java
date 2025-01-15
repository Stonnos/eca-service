package com.ecaservice.load.test.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    private static final int SCALE = 2;
    private static final int THOUSAND = 1000;

    /**
     * Calculates tps value.
     *
     * @param started     - started date
     * @param finished    - finished date
     * @param numRequests - requests number
     * @return tps value
     */
    public static BigDecimal tps(LocalDateTime started, LocalDateTime finished, Long numRequests) {
        if (started != null && finished != null && numRequests != null && numRequests > 0) {
            long totalTimeMillis = ChronoUnit.MILLIS.between(started, finished);
            BigDecimal totalTimeSeconds = BigDecimal.valueOf(totalTimeMillis)
                    .divide(BigDecimal.valueOf(THOUSAND), SCALE, RoundingMode.HALF_UP);
            if (totalTimeMillis > 0) {
                return BigDecimal.valueOf(numRequests)
                        .divide(totalTimeSeconds, SCALE, RoundingMode.HALF_UP);
            } else {
                return BigDecimal.valueOf(numRequests);
            }
        } else {
            return null;
        }
    }
}
