package com.ecaservice.web.dto.model;

import com.ecaservice.web.dto.dictionary.EvaluationResultsStatusDictionary;
import lombok.RequiredArgsConstructor;

/**
 * Evaluation results status enum.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public enum EvaluationResultsStatus {

    /**
     * ERS report results has been successfully received
     */
    RESULTS_RECEIVED(EvaluationResultsStatusDictionary.RESULTS_RECEIVED_DESCRIPTION),

    /**
     * Classifier evaluation in progress
     */
    EVALUATION_IN_PROGRESS(EvaluationResultsStatusDictionary.EVALUATION_IN_PROGRESS_DESCRIPTION),

    /**
     * Classifier evaluation finished with error or timeout
     */
    EVALUATION_ERROR(EvaluationResultsStatusDictionary.EVALUATION_ERROR_DESCRIPTION),

    /**
     * Evaluation results not sent to ERS
     */
    RESULTS_NOT_SENT(EvaluationResultsStatusDictionary.RESULTS_NOT_SENT_DESCRIPTION),

    /**
     * Evaluation results not found in ERS
     */
    EVALUATION_RESULTS_NOT_FOUND(EvaluationResultsStatusDictionary.EVALUATION_RESULTS_NOT_FOUND_DESCRIPTION),

    /**
     * Unknown error
     */
    ERROR(EvaluationResultsStatusDictionary.ERROR_DESCRIPTION),

    /**
     * ERS web service unavailable
     */
    ERS_SERVICE_UNAVAILABLE(EvaluationResultsStatusDictionary.ERS_SERVICE_UNAVAILABLE_DESCRIPTION);

    private final String description;

    /**
     * Evaluation results status status description.
     *
     * @return ERS evaluation results status status description
     */
    public String getDescription() {
        return description;
    }
}
