package com.ecaservice.model.evaluation;

import eca.core.evaluation.EvaluationResults;
import lombok.Data;

/**
 * Classification results model.
 *
 * @author Roman Batygin
 */
@Data
public class ClassificationResult {

    /**
     * Evaluation results
     */
    private EvaluationResults evaluationResults;

    /**
     * Success evaluation?
     */
    private boolean success;

    /**
     * Error message
     */
    private String errorMessage;

}
