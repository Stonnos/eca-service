package com.ecaservice.oauth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Oauth2 config.
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
     * Grant types
     */
    private String[] grantTypes;

    /**
     * Scopes
     */
    private String[] scopes;

    /**
     * Is auto approve?
     */
    private Boolean autoApprove;
}
