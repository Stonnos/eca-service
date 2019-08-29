package com.ecaservice.mapping;

import com.ecaservice.dto.evaluation.GetEvaluationResultsResponse;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Implements map evaluation results from ERS.
 *
 * @author Roman Batygin
 */
@Mapper(uses = {StatisticsReportMapper.class, ClassificationCostsMapper.class})
public interface GetEvaluationResultsMapper {

    /**
     * Maps evaluation results from ERS to evaluation results dto model.
     *
     * @param evaluationResultsResponse - evaluation results response
     * @return evaluation results dto model
     */
    @Mappings({
            @Mapping(source = "statistics", target = "evaluationStatisticsDto")
    })
    EvaluationResultsDto map(GetEvaluationResultsResponse evaluationResultsResponse);
}
