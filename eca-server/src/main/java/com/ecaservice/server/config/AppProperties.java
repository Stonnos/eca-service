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
     * Maximum page size for paging requests
     */
    @NotNull
    private Integer maxPageSize;

    /**
     * Thread pool size for async tasks
     */
    @NotNull
    private Integer threadPoolSize;

    /**
     * Notification properties
     */
    @NotNull
    private NotificationProperties notifications;

    /**
     * Notification properties
     */
    @Data
    public static class NotificationProperties {

        /**
         * Web pushes enabled?
         */
        private Boolean webPushesEnabled;
        /**
         * Emails sending enabled?
         */
        private Boolean emailsEnabled;
    }
}
