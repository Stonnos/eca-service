package com.ecaservice.mapping;

import com.ecaservice.dto.evaluation.StatisticsReport;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import org.mapstruct.Mapper;

/**
 * Implements mapping statistics report to evaluation results dto model.
 *
 * @author Roman Batygin
 */
@Mapper
public interface StatisticsReportMapper {

    /**
     * Maps statistics report to classifier options response model.
     *
     * @param statisticsReport - statistics report
     * @return evaluation results dto model
     */
    EvaluationResultsDto map(StatisticsReport statisticsReport);
}
