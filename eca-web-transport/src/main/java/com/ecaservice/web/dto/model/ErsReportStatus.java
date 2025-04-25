package com.ecaservice.web.dto.model;

import com.ecaservice.web.dto.dictionary.ErsReportStatusDictionary;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * ERS report status enum.
 *
 * @author Roman Batygin
 */
@Getter
@RequiredArgsConstructor
public enum ErsReportStatus {

    /**
     * ERS report has been successfully fetched
     */
    FETCHED(ErsReportStatusDictionary.FETCHED_DESCRIPTION),

    /**
     * New experiment (ready to process)
     */
    EXPERIMENT_NEW(ErsReportStatusDictionary.NEW_EXPERIMENT_DESCRIPTION),

    /**
     * Experiment in progress status
     */
    EXPERIMENT_IN_PROGRESS(ErsReportStatusDictionary.EXPERIMENT_IN_PROGRESS_DESCRIPTION),

    /**
     * Experiment finished with error or timeout
     */
    EXPERIMENT_ERROR(ErsReportStatusDictionary.EXPERIMENT_ERROR_DESCRIPTION),

    /**
     * Experiment results not found
     */
    EXPERIMENT_RESULTS_NOT_FOUND(ErsReportStatusDictionary.EXPERIMENT_RESULTS_NOT_FOUND_DESCRIPTION),

    /**
     * Experiment canceled
     */
    EXPERIMENT_CANCELED(ErsReportStatusDictionary.EXPERIMENT_CANCEL_DESCRIPTION);

    private final String description;
}
