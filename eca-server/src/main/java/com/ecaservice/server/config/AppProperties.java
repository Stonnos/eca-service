package com.ecaservice.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Application properties.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("app")
public class AppProperties {

    /**
     * Thread pool size for async tasks
     */
    private Integer threadPoolSize;

    /**
     * Scheduler pool size
     */
    private Integer schedulerPoolSize;

    /**
     * Number of days for models storage
     */
    private Long numberOfDaysForStorage;

    /**
     * Page size for processing
     */
    private Integer pageSize;

    /**
     * Model download url expiration in days
     */
    private Integer modelDownloadUrlExpirationDays;

    /**
     * Short life url expiration in minutes
     */
    private Integer shortLifeUrlExpirationMinutes;

    /**
     * Remove model backoff minutes
     */
    private Integer removeModelBackoffMinutes;
}
