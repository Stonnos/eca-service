package com.ecaservice.server.service.statistics;

import com.ecaservice.server.model.TimeSeriesStatistics;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Defines a contract for retrieving aggregated time-series statistical data
 * over a specified date and time range.
 *
 * @author Roman Batygin
 */
public interface TimeSeriesDataFetcher {

    /**
     * Retrieves monthly aggregated time-series statistics within the specified period.
     *
     * @param startDate the start of the time range (inclusive); must not be {@code null}
     * @param endDate   the end of the time range (inclusive); must not be {@code null}
     * @return a {@link List} of {@link TimeSeriesStatistics} grouped by month,
     * or an empty list if no data is available for the given range
     * @throws IllegalArgumentException if {@code startDate} or {@code endDate} is {@code null},
     *                                  or if {@code startDate} is after {@code endDate}
     */
    List<TimeSeriesStatistics> fetchMonthlyStatistics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Retrieves daily aggregated time-series statistics within the specified period.
     *
     * @param startDate the start of the time range (inclusive); must not be {@code null}
     * @param endDate   the end of the time range (inclusive); must not be {@code null}
     * @return a {@link List} of {@link TimeSeriesStatistics} grouped by day,
     * or an empty list if no data is available for the given range
     * @throws IllegalArgumentException if {@code startDate} or {@code endDate} is {@code null},
     *                                  or if {@code startDate} is after {@code endDate}
     */
    List<TimeSeriesStatistics> fetchDailyStatistics(LocalDateTime startDate, LocalDateTime endDate);
}