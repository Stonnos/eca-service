package com.ecaservice.server.service.statistics;

import com.ecaservice.server.dto.TimePeriod;
import com.ecaservice.server.dto.TimePeriodGroupBy;
import com.ecaservice.server.model.TimeSeriesStatistics;
import com.ecaservice.server.dto.TimeSeriesStatisticsFilter;
import com.ecaservice.web.dto.model.ChartDataDto;
import com.ecaservice.web.dto.model.ChartDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for calculating time-series statistics.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TimeSeriesChartService {

    private static final DateTimeFormatter DAY_KEY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MONTH_KEY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * Calculates continuous time-series chart data based on the provided filter.
     * Missing dates or months in the range are filled with zero counts.
     *
     * @param filter - statistics filter criteria
     * @param dataFetcher - data fetcher
     * @return list of chart data points ordered chronologically
     */
    public ChartDto getTimeSeriesStatistics(TimeSeriesStatisticsFilter filter,
                                            TimeSeriesDataFetcher dataFetcher) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;
        LocalDateTime endDate;
        TimePeriod period = Optional.ofNullable(filter.getPeriod()).orElse(TimePeriod.MONTH);
        if (TimePeriod.CUSTOM.equals(period)) {
            startDate = Optional.ofNullable(filter.getFrom()).orElse(LocalDate.now()).atStartOfDay();
            endDate = Optional.ofNullable(filter.getTo()).orElse(LocalDate.now()).atTime(LocalTime.MAX);
        } else {
            startDate = period.resolveStartDate(now).with(LocalTime.MIN);
            endDate = now;
        }
        List<TimeSeriesStatistics> rawStats = getTimeSeriesData(filter.getGroupBy(), startDate, endDate, dataFetcher);
        Map<LocalDate, Long> statsMap = rawStats.stream()
                .collect(Collectors.toMap(
                        stat -> stat.getPeriodStart().toLocalDate(),
                        TimeSeriesStatistics::getRequestsCount
                ));
        return buildChartDto(startDate.toLocalDate(), endDate.toLocalDate(), filter.getGroupBy(), statsMap);
    }

    private List<TimeSeriesStatistics> getTimeSeriesData(TimePeriodGroupBy groupBy,
                                                         LocalDateTime startDate,
                                                         LocalDateTime endDate,
                                                         TimeSeriesDataFetcher dataFetcher) {
        if (TimePeriodGroupBy.MONTH.equals(groupBy)) {
            return dataFetcher.fetchMonthlyStatistics(startDate, endDate);
        } else {
            return dataFetcher.fetchDailyStatistics(startDate, endDate);
        }
    }

    private ChartDto buildChartDto(LocalDate start,
                                   LocalDate end,
                                   TimePeriodGroupBy groupBy,
                                   Map<LocalDate, Long> statsMap) {
        List<ChartDataDto> dataItems = new ArrayList<>();
        long total = 0L;
        if (TimePeriodGroupBy.MONTH.equals(groupBy)) {
            LocalDate current = start.withDayOfMonth(1);
            LocalDate lastMonth = end.withDayOfMonth(1);
            while (!current.isAfter(lastMonth)) {
                long count = statsMap.getOrDefault(current, 0L);
                total += count;
                dataItems.add(new ChartDataDto(
                        current.format(MONTH_KEY_FORMATTER),
                        current.format(MONTH_KEY_FORMATTER),
                        count
                ));
                current = current.plusMonths(1);
            }
        } else {
            LocalDate current = start;
            while (!current.isAfter(end)) {
                long count = statsMap.getOrDefault(current, 0L);
                total += count;
                dataItems.add(new ChartDataDto(
                        current.format(DAY_KEY_FORMATTER),
                        current.format(DAY_KEY_FORMATTER),
                        count
                ));
                current = current.plusDays(1);
            }
        }
        return ChartDto.builder()
                .total(total)
                .dataItems(dataItems)
                .build();
    }
}
