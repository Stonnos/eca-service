package com.ecaservice.oauth2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Authorization server config.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("auth-server")
public class AuthServerConfig {

    /**
     * Authorization server base url
     */
    private String baseUrl;

    /**
     * Client id
     */
    private String clientId;

    /**
     * Client secret
     */
    private String clientSecret;
}
