package com.ecaservice.mapping;

import com.ecaservice.dto.evaluation.GetEvaluationResultsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Implements map evaluation results from ERS.
 *
 * @author Roman Batygin
 */
@Mapper(uses = {StatisticsReportMapper.class, ClassificationCostsMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class GetEvaluationResultsMapper {

    /**
     * Maps evaluation results from ERS to evaluation results dto model.
     *
     * @param evaluationResultsResponse - evaluation results response
     * @return evaluation results dto model
     */
    @Mapping(source = "statistics", target = "evaluationStatisticsDto")
    public abstract EvaluationResultsDto map(GetEvaluationResultsResponse evaluationResultsResponse);

    @AfterMapping
    protected void mapEvaluationResultsStatus(GetEvaluationResultsResponse evaluationResultsResponse,
                                              @MappingTarget EvaluationResultsDto evaluationResultsDto) {
        EvaluationResultsStatus evaluationResultsStatus = handleEvaluationResultsStatus(evaluationResultsResponse);
        evaluationResultsDto.setEvaluationResultsStatus(
                new EnumDto(evaluationResultsStatus.name(), evaluationResultsStatus.getDescription()));
    }

    private EvaluationResultsStatus handleEvaluationResultsStatus(GetEvaluationResultsResponse response) {
        if (ResponseStatus.SUCCESS.equals(response.getStatus())) {
            return EvaluationResultsStatus.RESULTS_RECEIVED;
        } else if (ResponseStatus.RESULTS_NOT_FOUND.equals(response.getStatus())) {
            return EvaluationResultsStatus.EVALUATION_RESULTS_NOT_FOUND;
        } else {
            return EvaluationResultsStatus.ERROR;
        }
    }
}
