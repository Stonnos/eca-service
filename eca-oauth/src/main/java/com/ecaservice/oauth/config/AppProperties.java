package com.ecaservice.oauth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

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
}
