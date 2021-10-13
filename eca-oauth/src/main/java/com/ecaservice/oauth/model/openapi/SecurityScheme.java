package com.ecaservice.oauth.model.openapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 * @author Roman Batygin
 */
@Data
public class SecurityScheme {

    private String type;
    private String description;
    private String name;
    @JsonProperty("$ref")
    private String ref;
    private String in;
    private String scheme;
    private String bearerFormat;
    private Oauth2Flows flows;
    private String openIdConnectUrl;
}
