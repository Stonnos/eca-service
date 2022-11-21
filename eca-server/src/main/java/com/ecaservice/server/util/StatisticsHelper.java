package com.ecaservice.server.util;

import com.ecaservice.server.model.entity.RequestStatusVisitor;
import com.ecaservice.server.model.projections.RequestStatusStatistics;
import com.ecaservice.web.dto.model.ChartDataDto;
import com.ecaservice.web.dto.model.ChartDto;
import com.ecaservice.web.dto.model.FilterDictionaryDto;
import com.ecaservice.web.dto.model.RequestStatusStatisticsDto;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Statistics helper class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class StatisticsHelper {

    /**
     * Calculates request statuses statistics.
     *
     * @param requestStatusStatistics - request statuses statistics list
     * @return request status statistics dto
     */
    public static RequestStatusStatisticsDto calculateRequestStatusesStatistics(
            List<RequestStatusStatistics> requestStatusStatistics) {
        RequestStatusStatisticsDto requestStatusStatisticsDto = new RequestStatusStatisticsDto();
        requestStatusStatistics.forEach(item -> item.getRequestStatus().handle(
                new RequestStatusVisitor<Void, RequestStatusStatisticsDto>() {
                    @Override
                    public Void caseNew(RequestStatusStatisticsDto statisticsDto) {
                        statisticsDto.setNewRequestsCount(item.getRequestsCount());
                        return null;
                    }

                    @Override
                    public Void caseFinished(RequestStatusStatisticsDto statisticsDto) {
                        statisticsDto.setFinishedRequestsCount(item.getRequestsCount());
                        return null;
                    }

                    @Override
                    public Void caseTimeout(RequestStatusStatisticsDto statisticsDto) {
                        statisticsDto.setTimeoutRequestsCount(item.getRequestsCount());
                        return null;
                    }

                    @Override
                    public Void caseError(RequestStatusStatisticsDto statisticsDto) {
                        statisticsDto.setErrorRequestsCount(item.getRequestsCount());
                        return null;
                    }

                    @Override
                    public Void caseInProgress(RequestStatusStatisticsDto statisticsDto) {
                        statisticsDto.setInProgressRequestsCount(item.getRequestsCount());
                        return null;
                    }
                }, requestStatusStatisticsDto));
        long total = requestStatusStatistics.stream().mapToLong(RequestStatusStatistics::getRequestsCount).sum();
        requestStatusStatisticsDto.setTotalCount(total);
        return requestStatusStatisticsDto;
    }

    /**
     * Calculates chart data from statistics map and dictionary.
     *
     * @param filterDictionaryDto - dictionary dto
     * @param statisticsMap       - statistics map
     * @return chart data
     */
    public static ChartDto calculateChartData(FilterDictionaryDto filterDictionaryDto,
                                              Map<String, Long> statisticsMap) {
        var chartDataItems = filterDictionaryDto.getValues()
                .stream()
                .map(filterDictionaryValueDto -> {
                    var chartDataDto = new ChartDataDto();
                    chartDataDto.setName(filterDictionaryValueDto.getValue());
                    chartDataDto.setLabel(filterDictionaryValueDto.getLabel());
                    chartDataDto.setCount(
                            statisticsMap.getOrDefault(filterDictionaryValueDto.getValue(), 0L));
                    return chartDataDto;
                })
                .collect(Collectors.toList());
        Long total = chartDataItems.stream().mapToLong(ChartDataDto::getCount).sum();
        return ChartDto.builder()
                .dataItems(chartDataItems)
                .total(total)
                .build();
    }
}
