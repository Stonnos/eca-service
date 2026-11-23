package com.ecaservice.server.service.evaluation;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.dto.TimePeriod;
import com.ecaservice.server.dto.TimePeriodGroupBy;
import com.ecaservice.server.dto.TimeSeriesStatisticsFilter;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.statistics.TimeSeriesChartService;
import com.ecaservice.web.dto.model.ChartDataDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Integration tests for {@link EvaluationLogStatisticsService}.
 *
 * @author Roman Batygin
 */
@Import({EvaluationLogStatisticsService.class, TimeSeriesChartService.class})
public class EvaluationLogStatisticsServiceTest extends AbstractJpaTest {

    private static final DateTimeFormatter DAY_KEY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MONTH_KEY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Autowired
    private EvaluationLogRepository evaluationLogRepository;

    @Autowired
    private InstancesInfoRepository instancesInfoRepository;

    @Autowired
    private EvaluationLogStatisticsService statisticsService;

    @Override
    public void deleteAll() {
        evaluationLogRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    @DisplayName("Daily statistics: groups records properly and fills missing days with zeros")
    void testShouldCalculateDailyStatisticsWithZeroGaps() {
        // Given: dates relative to the current day (today, yesterday, two days ago, and 100 days ago)
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate twoDaysAgo = today.minusDays(2);

        saveEvaluationLog(today.minusDays(100).atTime(12, 0)); // Should be excluded (older than THREE_MONTHS period)
        saveEvaluationLog(twoDaysAgo.atTime(9, 15));
        saveEvaluationLog(twoDaysAgo.atTime(18, 45));
        saveEvaluationLog(today.atTime(LocalTime.MIN));       // Start of the current day

        TimeSeriesStatisticsFilter filter = new TimeSeriesStatisticsFilter();
        filter.setPeriod(TimePeriod.THREE_MONTHS);
        filter.setGroupBy(TimePeriodGroupBy.DAY);

        // When
        var chartData = statisticsService.getTimeSeriesStatistics(filter);
        List<ChartDataDto> result = chartData.getDataItems();

        // Then: verify the last 3 days (two days ago, yesterday, today)
        List<ChartDataDto> lastThreeDays = result.subList(result.size() - 3, result.size());

        assertThat(chartData.getTotal()).isEqualTo(3L);
        assertThat(lastThreeDays)
                .extracting(ChartDataDto::getName, ChartDataDto::getLabel, ChartDataDto::getCount)
                .containsExactly(
                        tuple(twoDaysAgo.format(DAY_KEY_FORMATTER), twoDaysAgo.format(DAY_KEY_FORMATTER), 2L),
                        tuple(yesterday.format(DAY_KEY_FORMATTER), yesterday.format(DAY_KEY_FORMATTER), 0L),
                        tuple(today.format(DAY_KEY_FORMATTER), today.format(DAY_KEY_FORMATTER), 1L)
                );
    }

    @Test
    @DisplayName("Monthly statistics: groups records by full calendar months and fills missing months with zeros")
    void testShouldCalculateMonthlyStatisticsWithZeroGaps() {
        // Given: logs from 2 months ago and the current month (last month is skipped, 3 months ago is excluded)
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate oneMonthAgo = currentMonth.minusMonths(1);
        LocalDate twoMonthsAgo = currentMonth.minusMonths(2);
        LocalDate threeMonthsAgo = currentMonth.minusMonths(3);

        saveEvaluationLog(threeMonthsAgo.atTime(15, 0));   // Should be excluded (outside 3 full calendar months)
        saveEvaluationLog(twoMonthsAgo.atTime(10, 0));
        saveEvaluationLog(twoMonthsAgo.plusDays(10).atTime(14, 0));
        saveEvaluationLog(currentMonth.atTime(0, 0));
        saveEvaluationLog(LocalDate.now().atStartOfDay()); // Guaranteed to be within current month and <= now

        TimeSeriesStatisticsFilter filter = new TimeSeriesStatisticsFilter();
        filter.setPeriod(TimePeriod.THREE_MONTHS);
        filter.setGroupBy(TimePeriodGroupBy.MONTH);
        // When
        var chartData = statisticsService.getTimeSeriesStatistics(filter);
        List<ChartDataDto> result = chartData.getDataItems();
        // Then: strictly 3 calendar months (twoMonthsAgo, oneMonthAgo, currentMonth)
        assertThat(chartData.getTotal()).isEqualTo(4L);
        assertThat(result)
                .extracting(ChartDataDto::getName, ChartDataDto::getLabel, ChartDataDto::getCount)
                .containsExactly(
                        tuple(formatMonthKey(twoMonthsAgo), formatMonthKey(twoMonthsAgo), 2L),
                        tuple(formatMonthKey(oneMonthAgo), formatMonthKey(oneMonthAgo), 0L),
                        tuple(formatMonthKey(currentMonth), formatMonthKey(currentMonth), 2L)
                );
    }

    @Test
    @DisplayName("Default filter: uses current month grouped by day when filter fields are null")
    void testShouldUseDefaultFilterValuesWhenNotSpecified() {
        // Given
        LocalDate today = LocalDate.now();
        saveEvaluationLog(today.atStartOfDay());

        TimeSeriesStatisticsFilter emptyFilter = new TimeSeriesStatisticsFilter();
        emptyFilter.setGroupBy(null);
        // When
        var chartData = statisticsService.getTimeSeriesStatistics(emptyFilter);
        // Then: from 1st day of current month up to today inclusive
        assertThat(chartData.getTotal()).isEqualTo(1L);
        assertThat(chartData.getDataItems()).hasSize(today.getDayOfMonth());
    }

    @Test
    @DisplayName("Custom period daily statistics: filters records within explicit date range and fills missing days with zeros")
    void testShouldCalculateCustomPeriodDailyStatistics() {
        // Given: explicit 3-day custom range with records before, inside, and after the range
        LocalDate startDate = LocalDate.of(2026, 5, 10);
        LocalDate middleDate = LocalDate.of(2026, 5, 11);
        LocalDate endDate = LocalDate.of(2026, 5, 12);

        saveEvaluationLog(startDate.minusDays(1).atTime(23, 59)); // Excluded (before startDate)
        saveEvaluationLog(startDate.atStartOfDay());               // Included (exact start boundary)
        saveEvaluationLog(startDate.atTime(15, 30));               // Included
        saveEvaluationLog(endDate.atTime(23, 59, 59));             // Included (end of endDate)
        saveEvaluationLog(endDate.plusDays(1).atStartOfDay());     // Excluded (after endDate)

        TimeSeriesStatisticsFilter filter = new TimeSeriesStatisticsFilter();
        filter.setPeriod(TimePeriod.CUSTOM);
        filter.setFrom(startDate);
        filter.setTo(endDate);
        filter.setGroupBy(TimePeriodGroupBy.DAY);

        // When
        var chartData = statisticsService.getTimeSeriesStatistics(filter);
        List<ChartDataDto> result = chartData.getDataItems();

        // Then: strictly 3 days (startDate, middleDate with 0 count, endDate)
        assertThat(chartData.getTotal()).isEqualTo(3L);
        assertThat(result)
                .extracting(ChartDataDto::getName, ChartDataDto::getLabel, ChartDataDto::getCount)
                .containsExactly(
                        tuple(startDate.format(DAY_KEY_FORMATTER), startDate.format(DAY_KEY_FORMATTER), 2L),
                        tuple(middleDate.format(DAY_KEY_FORMATTER), middleDate.format(DAY_KEY_FORMATTER), 0L),
                        tuple(endDate.format(DAY_KEY_FORMATTER), endDate.format(DAY_KEY_FORMATTER), 1L)
                );
    }

    @Test
    @DisplayName("Custom period monthly statistics: groups records by months within explicit date range")
    void testShouldCalculateCustomPeriodMonthlyStatistics() {
        // Given: custom range spanning 3 calendar months (Jan - Mar 2026), skipping Feb
        LocalDate startDate = LocalDate.of(2026, 1, 15);
        LocalDate endDate = LocalDate.of(2026, 3, 20);

        saveEvaluationLog(LocalDate.of(2025, 12, 31).atTime(23, 59)); // Excluded (before startDate)
        saveEvaluationLog(LocalDate.of(2026, 1, 15).atTime(10, 0));   // Included (Jan)
        saveEvaluationLog(LocalDate.of(2026, 3, 5).atTime(12, 0));    // Included (Mar)
        saveEvaluationLog(LocalDate.of(2026, 3, 20).atTime(18, 0));   // Included (Mar)
        saveEvaluationLog(LocalDate.of(2026, 3, 21).atTime(9, 0));    // Excluded (after endDate)

        TimeSeriesStatisticsFilter filter = new TimeSeriesStatisticsFilter();
        filter.setPeriod(TimePeriod.CUSTOM);
        filter.setFrom(startDate);
        filter.setTo(endDate);
        filter.setGroupBy(TimePeriodGroupBy.MONTH);

        // When
        var chartData = statisticsService.getTimeSeriesStatistics(filter);
        List<ChartDataDto> result = chartData.getDataItems();

        // Then: strictly 3 months (2026-01, 2026-02 with 0 count, 2026-03)
        assertThat(chartData.getTotal()).isEqualTo(3L);
        assertThat(result)
                .extracting(ChartDataDto::getName, ChartDataDto::getLabel, ChartDataDto::getCount)
                .containsExactly(
                        tuple("2026-01", "2026-01", 1L),
                        tuple("2026-02", "2026-02", 0L),
                        tuple("2026-03", "2026-03", 2L)
                );
    }

    private void saveEvaluationLog(LocalDateTime creationDate) {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        evaluationLog.setCreationDate(creationDate);
        instancesInfoRepository.save(evaluationLog.getInstancesInfo());
        evaluationLogRepository.save(evaluationLog);
    }

    private String formatMonthKey(LocalDate date) {
        return date.format(MONTH_KEY_FORMATTER);
    }
}
