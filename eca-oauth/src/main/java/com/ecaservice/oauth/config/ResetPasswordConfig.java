package com.ecaservice.oauth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Reset password config.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("reset-password")
public class ResetPasswordConfig {

    /**
     * Reset password request validity in minutes
     */
    private Long validityMinutes;

    /**
     * Reset password base url
     */
    private String baseUrl;
}
