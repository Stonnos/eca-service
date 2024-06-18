package com.ecaservice.oauth.config;

import lombok.Data;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Oauth2 registered client.
 *
 * @author Roman Batygin
 */
@Data
public class Oauth2RegisteredClient {

    private static final int DEFAULT_ACCESS_TOKEN_TTL_MINUTES = 30;
    private static final int DEFAULT_REFRESH_TOKEN_TTL_MINUTES = 43200;

    /**
     * Client id
     */
    private String clientId;

    /**
     * Client secret
     */
    private String clientSecret;

    /**
     * Authorization grant types
     */
    private List<String> authorizationGrantTypes = newArrayList();

    /**
     * Scopes
     */
    private List<String> scopes = newArrayList();

    /**
     * Access token time to live minutes
     */
    private Integer accessTokenTimeToLiveMinutes = DEFAULT_ACCESS_TOKEN_TTL_MINUTES;

    /**
     * Refresh token time to live minutes
     */
    private Integer refreshTokenTimeToLiveMinutes = DEFAULT_REFRESH_TOKEN_TTL_MINUTES;
}
