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
     * Date format for date attributes
     */
    private String dateFormat;

    /**
     * Batch size for pagination
     */
    private Integer batchSize;

    /**
     * Request timeout in minutes
     */
    private Long requestTimeoutMinutes;

    /**
     * Request cache duration minutes (used for message correlation)
     */
    private Long requestCacheDurationMinutes;

    /**
     * Classifiers path on file system
     */
    private String classifiersPath;

    /**
     * Train data path on file system
     */
    private String trainDataPath;

    /**
     * Models downloading base url
     */
    private String downloadBaseUrl;

    /**
     * Days number for classifiers models files storage
     */
    private Integer numberOfDaysForStorage;
}
