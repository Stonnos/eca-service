package com.ecaservice.configuation.oauth2;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Oauth2 test config.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("oauth2-test")
public class Oauth2TestConfig {

    /**
     * User login
     */
    private String username;

    /**
     * User password
     */
    private String password;

    /**
     * Client id
     */
    private String clientId;

    /**
     * Client secret
     */
    private String secret;

    /**
     * Scope
     */
    private String scope;

    /**
     * Available grant types
     */
    private String[] grantTypes;
}