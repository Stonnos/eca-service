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

    private static final int DEFAULT_MAXIMUM_PAGES_NUM = 99;

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
     * Maximum pages number
     */
    private Integer maxPagesNum = DEFAULT_MAXIMUM_PAGES_NUM;

    /**
     * Model download url expiration in days
     */
    private Integer modelDownloadUrlExpirationDays;

    /**
     * Short life url expiration in minutes
     */
    private Integer shortLifeUrlExpirationMinutes;

    /**
     * Models cache time to live seconds
     */
    private Long modelCacheTtlSeconds;

    /**
     * Model cache size
     */
    private Integer modelCacheSize;

    /**
     * Auto remove expired models
     */
    private boolean autoRemoveExpiredModels;
}
