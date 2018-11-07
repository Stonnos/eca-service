package com.ecaservice.web.dto;

import lombok.Data;

/**
 * Request status statistics dto model.
 *
 * @author Roman Batygin
 */
@Data
public class RequestStatusStatisticsDto {

    /**
     * Total requests count
     */
    private long totalCount;

    /**
     * Requests count with NEW status
     */
    private long newRequestsCount;

    /**
     * Requests count with FINISHED status
     */
    private long finishedRequestsCount;

    /**
     * Requests count with TIMEOUT status
     */
    private long timeoutRequestsCount;

    /**
     * Requests count with ERROR status
     */
    private long errorRequestsCount;
}
