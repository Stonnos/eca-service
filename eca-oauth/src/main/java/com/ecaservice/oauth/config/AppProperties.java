package com.ecaservice.oauth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Application properties.
 *
 * @author Roman Batygin
 */
@Data
@Validated
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
     * Emails sending enabled?
     */
    private Boolean emailsEnabled;

    /**
     * Web application external base url
     */
    @NotEmpty
    private String webExternalBaseUrl;

    /**
     * Reset password properties
     */
    @NotNull
    private TokenValidityProperties resetPassword = new TokenValidityProperties();

    /**
     * Change password properties
     */
    @NotNull
    private TokenValidityProperties changePassword = new TokenValidityProperties();

    /**
     * Change email properties
     */
    @NotNull
    private TokenValidityProperties changeEmail = new TokenValidityProperties();

    /**
     * Token validity properties
     */
    @Data
    public static class TokenValidityProperties {

        /**
         * Token validity in minutes
         */
        @NotNull
        private Long validityMinutes;

        /**
         * Token url
         */
        @NotEmpty
        private String url;
    }
}
