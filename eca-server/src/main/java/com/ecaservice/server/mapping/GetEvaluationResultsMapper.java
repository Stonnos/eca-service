package com.ecaservice.server.mapping;

import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Implements map evaluation results from ERS.
 *
 * @author Roman Batygin
 */
@Mapper(uses = {StatisticsReportMapper.class, ClassificationCostsMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface GetEvaluationResultsMapper {

    /**
     * Maps evaluation results from ERS to evaluation results dto model.
     *
     * @param evaluationResultsResponse - evaluation results response
     * @return evaluation results dto model
     */
    @Mapping(source = "statistics", target = "evaluationStatisticsDto")
    EvaluationResultsDto map(GetEvaluationResultsResponse evaluationResultsResponse);
}
