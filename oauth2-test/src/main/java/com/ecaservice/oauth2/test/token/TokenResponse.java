package com.ecaservice.oauth2.test.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Oauth2 token response.
 *
 * @author Roman Batygin
 */
@Data
public class TokenResponse {

    /**
     * Access token
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * Token type
     */
    @JsonProperty("token_type")
    private String tokenType;

    /**
     * Refresh token
     */
    @JsonProperty("refresh_token")
    private String refreshToken;

    /**
     * Access token expires time seconds
     */
    @JsonProperty("expires_in")
    private Long expiresIn;

    /**
     * Scope
     */
    private String scope;
}