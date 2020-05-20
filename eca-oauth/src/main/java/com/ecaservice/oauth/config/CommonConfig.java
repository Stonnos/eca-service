package com.ecaservice.oauth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Application common config.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("common")
public class CommonConfig {

    /**
     * Maximum page size for paging requests
     */
    private Integer maxPageSize;
}