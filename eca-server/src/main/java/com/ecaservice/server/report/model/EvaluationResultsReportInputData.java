package com.ecaservice.server.report.model;

import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.server.model.entity.AbstractEvaluationEntity;
import lombok.Builder;
import lombok.Data;

/**
 * Evaluation results report input data.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class EvaluationResultsReportInputData {

    /**
     * Evaluation entity
     */
    private AbstractEvaluationEntity evaluationEntity;

    /**
     * Classifier options
     */
    private String classifierOptions;

    /**
     * Evaluation results response
     */
    private GetEvaluationResultsResponse evaluationResultsResponse;
}
