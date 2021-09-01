package com.ecaservice.oauth.config;

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
     * Maximum page size for paging requests
     */
    private Integer maxPageSize;

    /**
     * Emails sending enabled?
     */
    private Boolean emailsEnabled;
}
