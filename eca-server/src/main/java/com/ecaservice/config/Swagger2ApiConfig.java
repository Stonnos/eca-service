package com.ecaservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Swagger2 api info config.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("swagger2")
public class Swagger2ApiConfig {

    /**
     * Project version
     */
    private String projectVersion;

    /**
     * Api title
     */
    private String title;

    /**
     * Api description
     */
    private String description;

    /**
     * Api author full name
     */
    private String author;

    /**
     * Api email
     */
    private String email;

    /**
     * Application id
     */
    private String clientId;

    /**
     * Application secret
     */
    private String secret;

    /**
     * Oauth2 server url
     */
    private String oauthUrl;
}
