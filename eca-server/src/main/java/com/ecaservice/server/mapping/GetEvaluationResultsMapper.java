package com.ecaservice.server.mapping;

import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.server.model.evaluation.ConfusionMatrixCellData;
import com.ecaservice.server.model.evaluation.ConfusionMatrixData;
import com.ecaservice.web.dto.model.ConfusionMatrixCellDto;
import com.ecaservice.web.dto.model.ConfusionMatrixDto;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

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
    @Mapping(target = "confusionMatrix", ignore = true)
    EvaluationResultsDto map(GetEvaluationResultsResponse evaluationResultsResponse);

    /**
     * Maps confusion matrix data to dto model.
     *
     * @param confusionMatrixData - confusion matrix data
     * @return confusion matrix dto
     */
    ConfusionMatrixDto map(ConfusionMatrixData confusionMatrixData);

    /**
     * Maps confusion matrix cell data to dto model.
     *
     * @param confusionMatrixCellData - confusion matrix cell data
     * @return confusion matrix cell dto
     */
    ConfusionMatrixCellDto map(ConfusionMatrixCellData confusionMatrixCellData);

    /**
     * Maps confusion matrix cells data to dto model list.
     *
     * @param confusionMatrixCells - confusion matrix cells data
     * @return confusion matrix cells dto list
     */
    List<ConfusionMatrixCellDto> map(List<ConfusionMatrixCellData> confusionMatrixCells);
}
