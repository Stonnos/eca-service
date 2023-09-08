package com.ecaservice.data.loader.config;

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
     * Instances object expiration time in days
     */
    private Integer instancesExpireDays;

    /**
     * Remove data cron expression
     */
    private String removeDataCron;
}
