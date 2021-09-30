package com.ecaservice.server.mapping;

import com.ecaservice.ers.dto.StatisticsReport;
import com.ecaservice.web.dto.model.EvaluationStatisticsDto;
import org.mapstruct.Mapper;

/**
 * Implements mapping statistics report to evaluation results dto model.
 *
 * @author Roman Batygin
 */
@Mapper
public interface StatisticsReportMapper {

    /**
     * Maps statistics report to evaluation statistics dto model.
     *
     * @param statisticsReport - statistics report
     * @return evaluation statistics dto model
     */
    EvaluationStatisticsDto map(StatisticsReport statisticsReport);
}
