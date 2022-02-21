package com.ecaservice.core.redelivery.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Retry context model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetryContext {

    /**
     * Request id
     */
    private String requestId;

    /**
     * Current retries
     */
    private int currentRetries;

    /**
     * Maximum retires
     */
    private int maxRetries;
}
