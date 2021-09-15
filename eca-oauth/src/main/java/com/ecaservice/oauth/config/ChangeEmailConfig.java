package com.ecaservice.oauth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Change email config.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("change-email")
public class ChangeEmailConfig {

    /**
     * Change email request validity in hours
     */
    private Long validityHours;

    /**
     * Change email base url
     */
    private String baseUrl;
}
