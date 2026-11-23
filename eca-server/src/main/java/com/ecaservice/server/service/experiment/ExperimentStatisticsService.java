package com.ecaservice.server.service.experiment;

import com.ecaservice.server.dto.TimeSeriesStatisticsFilter;
import com.ecaservice.server.model.TimeSeriesStatistics;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.service.statistics.TimeSeriesChartService;
import com.ecaservice.server.service.statistics.TimeSeriesDataFetcher;
import com.ecaservice.web.dto.model.ChartDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for calculating experiments statistics.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentStatisticsService {

    private final ExperimentRepository experimentRepository;
    private final TimeSeriesChartService timeSeriesChartService;

    /**
     * Calculates continuous time-series chart data for experiments based on the provided filter.
     * Missing dates or months in the range are filled with zero counts.
     *
     * @param filter - statistics filter criteria
     * @return list of chart data points ordered chronologically
     */
    public ChartDto getTimeSeriesStatistics(TimeSeriesStatisticsFilter filter) {
        log.info("Gets experiments time series statistics: {}", filter);
        var result = timeSeriesChartService.getTimeSeriesStatistics(filter, new TimeSeriesDataFetcher() {
            @Override
            public List<TimeSeriesStatistics> fetchMonthlyStatistics(LocalDateTime startDate, LocalDateTime endDate) {
                return experimentRepository.getMonthlyTimeSeriesStatistics(startDate, endDate);
            }

            @Override
            public List<TimeSeriesStatistics> fetchDailyStatistics(LocalDateTime startDate, LocalDateTime endDate) {
                return experimentRepository.getDailyTimeSeriesStatistics(startDate, endDate);
            }
        });
        log.info("Experiments time series statistics rows [{}] has been fetched for filter: {}", result.getTotal(),
                filter);
        return result;
    }
}
