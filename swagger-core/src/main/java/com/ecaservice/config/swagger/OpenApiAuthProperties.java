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
     * Scopes list
     */
    private List<String> scopes;
}
