package com.ecaservice.ers.mapping;

import com.ecaservice.ers.dto.StatisticsReport;
import com.ecaservice.ers.model.StatisticsInfo;
import org.mapstruct.Mapper;

/**
 * Statistics report mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface StatisticsReportMapper {

    /**
     * Maps statistics report to statistics info entity.
     *
     * @param statisticsReport - statistics report
     * @return statistics info entity
     */
    StatisticsInfo map(StatisticsReport statisticsReport);

    /**
     * Maps statistics info entity to statistics report.
     *
     * @param statisticsInfo - statistics info
     * @return statistics report
     */
    StatisticsReport map(StatisticsInfo statisticsInfo);
}
