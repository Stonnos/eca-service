package com.ecaservice.server.mapping;

import com.ecaservice.server.bpm.model.EvaluationResultsModel;
import com.ecaservice.server.model.evaluation.EvaluationResultsDataModel;
import eca.core.evaluation.Evaluation;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;

/**
 * Evaluation results model mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface EvaluationResultsModelMapper {

    /**
     * Maps evaluation results bpmn model to evaluation results model.
     *
     * @param evaluationResultsDataModel - evaluation results bpmn model
     * @return evaluation results model
     */
    @Mapping(source = "status", target = "requestStatus")
    EvaluationResultsModel map(EvaluationResultsDataModel evaluationResultsDataModel);

    /**
     * Post mapping data.
     *
     * @param evaluationResultsDataModel - evaluation results bpmn data model
     * @param evaluationResultsModel     - evaluation results model
     */
    @AfterMapping
    default void postMappingEvaluationResults(EvaluationResultsDataModel evaluationResultsDataModel,
                                              @MappingTarget EvaluationResultsModel evaluationResultsModel) {
        if (evaluationResultsDataModel.getEvaluationResults() != null) {
            Evaluation evaluation = evaluationResultsDataModel.getEvaluationResults().getEvaluation();
            evaluationResultsModel.setNumTestInstances((int) evaluation.numInstances());
            evaluationResultsModel.setNumCorrect((int) evaluation.correct());
            evaluationResultsModel.setNumIncorrect((int) evaluation.incorrect());
            evaluationResultsModel.setPctCorrect(BigDecimal.valueOf(evaluation.pctCorrect()));
            evaluationResultsModel.setPctIncorrect(BigDecimal.valueOf(evaluation.pctIncorrect()));
            evaluationResultsModel.setMeanAbsoluteError(BigDecimal.valueOf(evaluation.meanAbsoluteError()));
        }
    }
}
