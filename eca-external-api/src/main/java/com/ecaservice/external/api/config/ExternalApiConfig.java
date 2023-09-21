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
     * Evaluation request timeout in minutes
     */
    private Long evaluationRequestTimeoutMinutes;

    /**
     * Experiment request timeout in minutes
     */
    private Long experimentRequestTimeoutMinutes;

    /**
     * Classifier download url expiration in days
     */
    private Integer classifierDownloadUrlExpirationDays;
}
