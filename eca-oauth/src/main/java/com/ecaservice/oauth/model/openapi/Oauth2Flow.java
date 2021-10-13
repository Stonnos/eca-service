package com.ecaservice.oauth.model.openapi;

import lombok.Data;

import java.util.Map;

/**
 *
 * @author Roman Batygin
 */
@Data
public class Oauth2Flow {
    private String authorizationUrl;
    private String tokenUrl;
    private String refreshUrl;
    private Map<String, String> scopes;
}
