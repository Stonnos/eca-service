package com.ecaservice.web.dto.model;

import com.ecaservice.web.dto.dictionary.ErsReportStatusDictionary;
import lombok.RequiredArgsConstructor;

/**
 * ERS report status enum.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public enum ErsReportStatus {

    /**
     * All experiment results has been sent to ERS service
     */
    SUCCESS_SENT(ErsReportStatusDictionary.SUCCESS_SENT_DESCRIPTION),

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
     * Experiment results for sending to ERS not found
     */
    EXPERIMENT_RESULTS_NOT_FOUND(ErsReportStatusDictionary.EXPERIMENT_RESULTS_NOT_FOUND_DESCRIPTION),

    /**
     * No experiment results were sent to ERS service and experiment files were deleted
     */
    EXPERIMENT_DELETED(ErsReportStatusDictionary.EXPERIMENT_DELETED_DESCRIPTION),

    /**
     * Some experiment results must be sent to ERS service
     */
    NOT_SENT(ErsReportStatusDictionary.NOT_SENT_DESCRIPTION);

    private final String description;

    /**
     * ERS report status description.
     *
     * @return ERS report status status description
     */
    public String getDescription() {
        return description;
    }
}
