package com.ecaservice.web.dto.model;

import com.ecaservice.web.dto.dictionary.ErsReportStatusDictionary;

/**
 * ERS report status enum.
 *
 * @author Roman Batygin
 */
public enum ErsReportStatus {

    /**
     * Experiment results has been sent to ERS service
     */
    SUCCESS_SENT(ErsReportStatusDictionary.SUCCESS_SENT_DESCRIPTION),

    /**
     * Experiment in progress status
     */
    EXPERIMENT_IN_PROGRESS(ErsReportStatusDictionary.EXPERIMENT_IN_PROGRESS_DESCRIPTION),

    /**
     * Experiment finished with error or timeout
     */
    EXPERIMENT_ERROR(ErsReportStatusDictionary.EXPERIMENT_ERROR_DESCRIPTION),

    /**
     * Experiment results hasn't been sent to ERS service and deleted
     */
    EXPERIMENT_DELETED(ErsReportStatusDictionary.EXPERIMENT_DELETED_DESCRIPTION),

    /**
     * Experiment results needs sending to ERS service
     */
    NEED_SENT(ErsReportStatusDictionary.NEED_SENT_DESCRIPTION);

    private String description;

    ErsReportStatus(String description) {
        this.description = description;
    }


    /**
     * ERS report status description.
     *
     * @return ERS report status status description
     */
    public String getDescription() {
        return description;
    }
}
