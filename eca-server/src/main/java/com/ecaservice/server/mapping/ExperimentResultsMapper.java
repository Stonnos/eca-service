package com.ecaservice.server.mapping;

import com.ecaservice.server.model.entity.ExperimentResultsEntity;
import com.ecaservice.web.dto.model.ExperimentResultsDetailsDto;
import com.ecaservice.web.dto.model.ExperimentResultsDto;
import eca.core.evaluation.EvaluationResults;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Experiment results mapper.
 *
 * @author Roman Batygin
 */
@Mapper(uses = {ClassifierInfoMapper.class, ExperimentMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class ExperimentResultsMapper {

    /**
     * Maps experiment evaluation results to its entity model.
     *
     * @param evaluationResults - evaluation results
     * @return experiment results entity
     */
    @Mapping(source = "classifier", target = "classifierInfo")
    public abstract ExperimentResultsEntity map(EvaluationResults evaluationResults);

    /**
     * Maps experiment results entity to its dto model.
     *
     * @param experimentResultsEntity - experiment results entity
     * @return experiment results dto
     */
    @Mapping(target = "classifierInfo", ignore = true)
    public abstract ExperimentResultsDto map(ExperimentResultsEntity experimentResultsEntity);

    /**
     * Maps experiment results entity and experiment results details.
     *
     * @param experimentResultsEntity - experiment results details mapper
     * @return experiment results details dto
     */
    @Mapping(source = "experiment", target = "experimentDto")
    @Mapping(target = "classifierInfo", ignore = true)
    public abstract ExperimentResultsDetailsDto mapDetails(ExperimentResultsEntity experimentResultsEntity);

    @AfterMapping
    protected void mapEvaluationResults(EvaluationResults evaluationResults,
                                        @MappingTarget ExperimentResultsEntity experimentResultsEntity) {
        if (Optional.ofNullable(evaluationResults.getEvaluation()).isPresent()) {
            experimentResultsEntity.setPctCorrect(BigDecimal.valueOf(evaluationResults.getEvaluation().pctCorrect()));
        }
    }
}
