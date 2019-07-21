package com.ecaservice.config.swagger;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

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
     * Swagger apis info
     */
    private Map<String, SwaggerApiInfo> groups;

    /**
     * Application id
     */
    private String clientId;

    /**
     * Application secret
     */
    private String secret;

    /**
     * Oauth2 token url
     */
    private String tokenUrl;
}
