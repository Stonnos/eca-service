package com.ecaservice.oauth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Change password config.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("change-password")
public class ChangePasswordConfig {

    /**
     * Change password request validity in minutes
     */
    private Long validityMinutes;

    /**
     * Change password base url
     */
    private String baseUrl;
}
