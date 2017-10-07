package com.ecaservice.dto;

import com.ecaservice.model.TechnicalStatus;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eca.core.evaluation.EvaluationResults;
import lombok.Data;

/**
 * Evaluation response model.
 *
 * @author Roman Batygin
 */
@Data
@JsonSerialize(using = EvaluationResponseSerializer.class)
public class EvaluationResponse {

    /**
     * Evaluation results
     */
    private EvaluationResults evaluationResults;

    /**
     * Technical status
     */
    private TechnicalStatus status;

    /**
     * Error message
     */
    private String errorMessage;
}
