package com.ecaservice.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Web application config.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("web-config")
public class WebConfig {

    /**
     * Server api url
     */
    private String apiUrl;

    /**
     * Auth server url
     */
    private String oauthUrl;

    /**
     * Client id
     */
    private String clientId;

    /**
     * Client secret
     */
    private String secret;
}