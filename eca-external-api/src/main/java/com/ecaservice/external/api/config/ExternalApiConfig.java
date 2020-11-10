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
     * Maximum page size for paging requests
     */
    private Integer maxPageSize;

    /**
     * Date format for date attributes
     */
    private String dateFormat;

    /**
     * Batch size for data saving
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
     * Models downloading base url
     */
    private String downloadBaseUrl;
}
