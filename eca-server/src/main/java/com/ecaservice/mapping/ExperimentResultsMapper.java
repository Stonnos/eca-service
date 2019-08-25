package com.ecaservice.mapping;

import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.web.dto.model.ExperimentResultsDto;
import eca.core.evaluation.EvaluationResults;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Experiment results mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class ExperimentResultsMapper {

    /**
     * Maps experiment evaluation results to its entity model.
     *
     * @param evaluationResults - evaluation results
     * @return experiment results entity
     */
    public abstract ExperimentResultsEntity map(EvaluationResults evaluationResults);

    /**
     * Maps experiment results entity to its dto model.
     *
     * @param experimentResultsEntity - experiment results entity
     * @return experiment results dto
     */
    public abstract ExperimentResultsDto map(ExperimentResultsEntity experimentResultsEntity);

    /**
     * Maps experiment results entities list to its dto list.
     *
     * @param experimentResultsEntityList - experiment results entity list
     * @return experiment results dto list
     */
    public abstract List<ExperimentResultsDto> map(List<ExperimentResultsEntity> experimentResultsEntityList);

    @AfterMapping
    protected void mapClassifier(EvaluationResults evaluationResults,
                                 @MappingTarget ExperimentResultsEntity experimentResultsEntity) {
        if (Optional.ofNullable(evaluationResults.getClassifier()).isPresent()) {
            experimentResultsEntity.setClassifierName(evaluationResults.getClassifier().getClass().getSimpleName());
        }
    }

    @AfterMapping
    protected void mapEvaluationResults(EvaluationResults evaluationResults,
                                        @MappingTarget ExperimentResultsEntity experimentResultsEntity) {
        if (Optional.ofNullable(evaluationResults.getEvaluation()).isPresent()) {
            experimentResultsEntity.setPctCorrect(BigDecimal.valueOf(evaluationResults.getEvaluation().pctCorrect()));
        }
    }
}
