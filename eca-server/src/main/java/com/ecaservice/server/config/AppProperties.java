package com.ecaservice.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * Application properties.
 *
 * @author Roman Batygin
 */
@Validated
@Data
@ConfigurationProperties("app")
public class AppProperties {

    /**
     * Thread pool size for async tasks
     */
    @NotNull
    private Integer threadPoolSize;

    /**
     * Notification properties
     */
    private NotificationProperties notifications = new NotificationProperties();

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
     * Notification properties
     */
    @Data
    public static class NotificationProperties {

        /**
         * Web pushes enabled?
         */
        private Boolean webPushesEnabled;
    }
}
