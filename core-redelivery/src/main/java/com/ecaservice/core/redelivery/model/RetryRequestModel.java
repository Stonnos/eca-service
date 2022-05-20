package com.ecaservice.core.redelivery.model;

import lombok.Builder;
import lombok.Data;

/**
 * Retry request internal model.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class RetryRequestModel {

    /**
     * Request type (code)
     */
    private String requestType;

    /**
     * Request id
     */
    private String requestId;

    /**
     * Request model
     */
    private String request;

    /**
     * Max. retries
     */
    private int maxRetries;

    /**
     * Min. retry interval
     */
    private long minRetryInterval;
}
