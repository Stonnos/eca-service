package com.ecaservice.server.service.experiment;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.dto.TimePeriod;
import com.ecaservice.server.dto.TimePeriodGroupBy;
import com.ecaservice.server.dto.TimeSeriesStatisticsFilter;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.repository.ExperimentRepository;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Integration tests for {@link ExperimentStatisticsService}.
 *
 * @author Roman Batygin
 */
@Import({ExperimentStatisticsService.class, TimeSeriesChartService.class})
public class ExperimentStatisticsServiceTest extends AbstractJpaTest {

    private static final DateTimeFormatter DAY_KEY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MONTH_KEY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Autowired
    private ExperimentRepository experimentRepository;

    @Autowired
    private InstancesInfoRepository instancesInfoRepository;

    @Autowired
    private ExperimentStatisticsService experimentStatisticsService;

    @Override
    public void deleteAll() {
        experimentRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    @DisplayName("Daily statistics: groups records properly and fills missing days with zeros")
    void testShouldCalculateDailyStatisticsWithZeroGaps() {
        // Given: dates relative to the current day (today, yesterday, two days ago, and 100 days ago)
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate twoDaysAgo = today.minusDays(2);

        saveExperiment(today.minusDays(100).atTime(12, 0)); // Should be excluded (older than THREE_MONTHS period)
        saveExperiment(twoDaysAgo.atTime(9, 15));
        saveExperiment(twoDaysAgo.atTime(18, 45));
        saveExperiment(today.atTime(LocalTime.MIN));       // Start of the current day

        TimeSeriesStatisticsFilter filter = new TimeSeriesStatisticsFilter();
        filter.setPeriod(TimePeriod.THREE_MONTHS);
        filter.setGroupBy(TimePeriodGroupBy.DAY);

        // When
        var chartData = experimentStatisticsService.getTimeSeriesStatistics(filter);
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

        saveExperiment(threeMonthsAgo.atTime(15, 0));   // Should be excluded (outside 3 full calendar months)
        saveExperiment(twoMonthsAgo.atTime(10, 0));
        saveExperiment(twoMonthsAgo.plusDays(10).atTime(14, 0));
        saveExperiment(currentMonth.atTime(0, 0));
        saveExperiment(LocalDate.now().atStartOfDay()); // Guaranteed to be within current month and <= now

        TimeSeriesStatisticsFilter filter = new TimeSeriesStatisticsFilter();
        filter.setPeriod(TimePeriod.THREE_MONTHS);
        filter.setGroupBy(TimePeriodGroupBy.MONTH);
        // When
        var chartData = experimentStatisticsService.getTimeSeriesStatistics(filter);
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
    @DisplayName("Specific day filter: counts logs strictly from 00:00:00 to 23:59:59")
    void testShouldCalculateStatisticsForSpecificDay() {
        // Given: fixed past date (current system time does not affect specific day filtering)
        LocalDate targetDay = LocalDate.of(2026, 5, 15);
        saveExperiment(LocalDateTime.of(2026, 5, 14, 23, 59, 59)); // Previous day (exclude)
        saveExperiment(LocalDateTime.of(2026, 5, 15, 0, 0, 0));    // Start of target day (include)
        saveExperiment(LocalDateTime.of(2026, 5, 15, 23, 59, 59)); // End of target day (include)
        saveExperiment(LocalDateTime.of(2026, 5, 16, 0, 0, 0));    // Next day (exclude)

        TimeSeriesStatisticsFilter filter = new TimeSeriesStatisticsFilter();
        filter.setDay(targetDay);
        filter.setPeriod(TimePeriod.YEAR); // Ignored since specific day is provided
        // When
        var chartData = experimentStatisticsService.getTimeSeriesStatistics(filter);
        List<ChartDataDto> result = chartData.getDataItems();
        // Then
        assertThat(chartData.getTotal()).isEqualTo(2L);
        assertThat(result)
                .hasSize(1)
                .extracting(ChartDataDto::getName, ChartDataDto::getLabel, ChartDataDto::getCount)
                .containsExactly(
                        tuple("2026-05-15", "2026-05-15", 2L)
                );
    }

    @Test
    @DisplayName("Empty database: returns zero-filled data points for the selected period")
    void testShouldReturnZeroFilledPointsWhenDatabaseIsEmpty() {
        // Given
        TimeSeriesStatisticsFilter filter = new TimeSeriesStatisticsFilter();
        filter.setDay(LocalDate.of(2026, 4, 10));
        // When
        var chartData = experimentStatisticsService.getTimeSeriesStatistics(filter);
        List<ChartDataDto> result = chartData.getDataItems();
        // Then
        assertThat(chartData.getTotal()).isEqualTo(0L);
        assertThat(result)
                .hasSize(1)
                .extracting(ChartDataDto::getName, ChartDataDto::getLabel, ChartDataDto::getCount)
                .containsExactly(
                        tuple("2026-04-10", "2026-04-10", 0L)
                );
    }

    @Test
    @DisplayName("Default filter: uses current month grouped by day when filter fields are null")
    void testShouldUseDefaultFilterValuesWhenNotSpecified() {
        // Given
        LocalDate today = LocalDate.now();
        saveExperiment(today.atStartOfDay());

        TimeSeriesStatisticsFilter emptyFilter = new TimeSeriesStatisticsFilter();
        emptyFilter.setGroupBy(null);
        // When
        var chartData = experimentStatisticsService.getTimeSeriesStatistics(emptyFilter);
        // Then: from 1st day of current month up to today inclusive
        assertThat(chartData.getTotal()).isEqualTo(1L);
        assertThat(chartData.getDataItems()).hasSize(today.getDayOfMonth());
    }

    private void saveExperiment(LocalDateTime creationDate) {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experiment.setCreationDate(creationDate);
        instancesInfoRepository.save(experiment.getInstancesInfo());
        experimentRepository.save(experiment);
    }

    private String formatMonthKey(LocalDate date) {
        return date.format(MONTH_KEY_FORMATTER);
    }
}
