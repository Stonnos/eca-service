package com.ecaservice.config.oauth2;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Oauth2 resource config.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("oauth2")
public class Oauth2Config {

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
