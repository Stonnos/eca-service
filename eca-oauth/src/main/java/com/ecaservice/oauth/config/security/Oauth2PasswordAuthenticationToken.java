package com.ecaservice.oauth.config.security;

import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

import java.util.Collections;

/**
 * Oauth2 password authentication token.
 *
 * @author Roman Batygin
 */
public class Oauth2PasswordAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    /**
     * Username
     */
    @Getter
    private final String username;

    /**
     * Password
     */
    @Getter
    private final String password;

    public Oauth2PasswordAuthenticationToken(Authentication clientPrincipal, String username, String password) {
        super(AuthorizationGrantType.PASSWORD, clientPrincipal, Collections.emptyMap());
        this.username = username;
        this.password = password;
    }
}