package com.ecaservice.oauth.security.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Oauth2 token response.
 *
 * @author Roman Batygin
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Oauth2TokenResponse {

    /**
     * Scopes
     */
    private String scope;

    /**
     * Token type
     */
    @JsonProperty("token_type")
    private String type;
}
