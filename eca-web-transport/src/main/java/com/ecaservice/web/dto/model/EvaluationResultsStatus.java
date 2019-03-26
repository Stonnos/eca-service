package com.ecaservice.web.dto.model;

/**
 * Evaluation results status enum.
 *
 * @author Roman Batygin
 */
public enum EvaluationResultsStatus {

    /**
     * ERS report results has been successfully received
     */
    RESULTS_RECEIVED,

    /**
     * Classifier evaluation in progress
     */
    EVALUATION_IN_PROGRESS,

    /**
     * Classifier evaluation finished with error or timeout
     */
    EVALUATION_ERROR,

    /**
     * Evaluation results not sent to ERS
     */
    RESULTS_NOT_SENT,

    /**
     * Evaluation results not found in ERS
     */
    EVALUATION_RESULTS_NOT_FOUND,

    /**
     * Unknown error
     */
    ERROR,

    /**
     * ERS web service unavailable
     */
    ERS_SERVICE_UNAVAILABLE
}
