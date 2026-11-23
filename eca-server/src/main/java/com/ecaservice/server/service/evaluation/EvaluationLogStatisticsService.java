package com.ecaservice.server.service.evaluation;

import com.ecaservice.server.dto.TimeSeriesStatisticsFilter;
import com.ecaservice.server.model.TimeSeriesStatistics;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.statistics.TimeSeriesChartService;
import com.ecaservice.server.service.statistics.TimeSeriesDataFetcher;
import com.ecaservice.web.dto.model.ChartDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for calculating time-series statistics of evaluation logs.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationLogStatisticsService {

    private final EvaluationLogRepository evaluationLogRepository;
    private final TimeSeriesChartService timeSeriesChartService;

    /**
     * Calculates continuous time-series chart data for evaluation logs based on the provided filter.
     * Missing dates or months in the range are filled with zero counts.
     *
     * @param filter - statistics filter criteria
     * @return list of chart data points ordered chronologically
     */
    public ChartDto getTimeSeriesStatistics(TimeSeriesStatisticsFilter filter) {
        log.info("Gets evaluation logs time series statistics: {}", filter);
        var result = timeSeriesChartService.getTimeSeriesStatistics(filter, new TimeSeriesDataFetcher() {
            @Override
            public List<TimeSeriesStatistics> fetchMonthlyStatistics(LocalDateTime startDate, LocalDateTime endDate) {
                return evaluationLogRepository.getMonthlyTimeSeriesStatistics(startDate, endDate);
            }

            @Override
            public List<TimeSeriesStatistics> fetchDailyStatistics(LocalDateTime startDate, LocalDateTime endDate) {
                return evaluationLogRepository.getDailyTimeSeriesStatistics(startDate, endDate);
            }
        });
        log.info("Evaluation logs time series statistics rows [{}] has been fetched for filter: {}", result.getTotal(),
                filter);
        return result;
    }
}
