package com.ecaservice.web.dto.model;

import lombok.Data;

/**
 * ERS report dto model.
 *
 * @author Roman Batygin
 */
@Data
public class ErsReportDto {

    /**
     * Experiment uuid
     */
    private String experimentUuid;

    /**
     * ERS requests count
     */
    private long requestsCount;

    /**
     * Successfully saved classifiers count
     */
    private long successfullySavedClassifiers;

    /**
     * Failed requests count
     */
    private long failedRequestsCount;

    /**
     * Ers report status
     */
    private ErsReportStatus ersReportStatus;
}
