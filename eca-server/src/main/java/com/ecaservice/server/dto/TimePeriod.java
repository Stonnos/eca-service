package com.ecaservice.server.dto;

import java.time.LocalDateTime;

/**
 * Predefined time periods for statistics filtering.
 *
 * @author Roman Batygin
 */
public enum TimePeriod {

    /**
     * Last 1 day
     */
    DAY,

    /**
     * Last 1 month
     */
    MONTH,

    /**
     * Last 3 months
     */
    THREE_MONTHS,

    /**
     * Last 6 months
     */
    SIX_MONTHS,

    /**
     * Last 1 year
     */
    YEAR;

    private static final int CURRENT_MONTH_OFFSET = 1;
    private static final int THREE_MONTHS_COUNT = 3;
    private static final int SIX_MONTHS_COUNT = 6;
    private static final int TWELVE_MONTHS_COUNT = 12;
    private static final int FIRST_DAY_OF_MONTH = 1;

    /**
     * Resolves start date-time relative to the given reference date-time.
     * Aligns multi-month periods to the 1st day of the starting calendar month.
     *
     * @param now - reference current date-time
     * @return resolved start date-time
     */
    public LocalDateTime resolveStartDate(LocalDateTime now) {
        return switch (this) {
            case DAY -> now;
            case MONTH -> now.withDayOfMonth(FIRST_DAY_OF_MONTH);
            case THREE_MONTHS -> now.minusMonths(THREE_MONTHS_COUNT - CURRENT_MONTH_OFFSET)
                    .withDayOfMonth(FIRST_DAY_OF_MONTH);
            case SIX_MONTHS -> now.minusMonths(SIX_MONTHS_COUNT - CURRENT_MONTH_OFFSET)
                    .withDayOfMonth(FIRST_DAY_OF_MONTH);
            case YEAR -> now.minusMonths(TWELVE_MONTHS_COUNT - CURRENT_MONTH_OFFSET)
                    .withDayOfMonth(FIRST_DAY_OF_MONTH);
        };
    }
}
