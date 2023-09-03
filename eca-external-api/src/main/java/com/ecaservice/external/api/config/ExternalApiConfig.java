package com.ecaservice.external.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * External API config.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("external-api")
public class ExternalApiConfig {

    /**
     * Batch size for pagination
     */
    private Integer batchSize;

    /**
     * Request timeout in seconds
     */
    private Long requestTimeoutSeconds;

    /**
     * Evaluation request timeout in minutes
     */
    private Long evaluationRequestTimeoutMinutes;

    /**
     * Experiment request timeout in minutes
     */
    private Long experimentRequestTimeoutMinutes;

    /**
     * Days number for classifiers models files storage
     */
    private Integer numberOfDaysForStorage;

    /**
     * Classifier download url expiration in days
     */
    private Integer classifierDownloadUrlExpirationDays;
}
