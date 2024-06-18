package com.ecaservice.oauth.security.model;

import com.ecaservice.oauth.util.Oauth2Utils;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

import java.util.Collections;

/**
 * Oauth2 tfa code authentication token.
 *
 * @author Roman Batygin
 */
public class Oauth2TfaCodeAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    /**
     * Token (request id)
     */
    @Getter
    private final String token;

    /**
     * Tfa code
     */
    @Getter
    private final String code;

    public Oauth2TfaCodeAuthenticationToken(Authentication clientPrincipal, String token, String code) {
        super(Oauth2Utils.TFA_CODE, clientPrincipal, Collections.emptyMap());
        this.token = token;
        this.code = code;
    }
}