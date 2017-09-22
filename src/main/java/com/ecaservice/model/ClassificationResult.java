package com.ecaservice.model;

import eca.core.evaluation.EvaluationResults;
import lombok.Data;

/**
 * Classification results model.
 *
 * @author Roman Batygin
 */
@Data
public class ClassificationResult {

    private EvaluationResults evaluationResults;

    private boolean success;

    private String errorMessage;

}
