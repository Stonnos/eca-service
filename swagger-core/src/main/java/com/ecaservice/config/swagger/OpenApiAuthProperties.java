package com.ecaservice.config.swagger;

import lombok.Data;

import java.util.List;

/**
 * Open api auth properties.
 *
 * @author Roman Batygin
 */
@Data
public class OpenApiAuthProperties {

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

    /**
     * Oauth2 refresh token url
     */
    private String refreshTokenUrl;

    /**
     * Scopes list
     */
    private List<String> scopes;
}
