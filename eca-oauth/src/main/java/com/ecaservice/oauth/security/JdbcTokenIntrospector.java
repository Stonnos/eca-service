package com.ecaservice.oauth.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import static com.ecaservice.oauth.util.Oauth2Utils.populateAuthorities;

/**
 * Jdbc token introspector.
 *
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor
public class JdbcTokenIntrospector implements OpaqueTokenIntrospector {

    private static final String SCOPE_PREFIX = "SCOPE_%s";

    private final OAuth2AuthorizationService oAuth2AuthorizationService;

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        OAuth2Authorization.Token<OAuth2Token> authorizedToken = getOAuth2Token(token);
        if (authorizedToken == null || !authorizedToken.isActive()) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN,
                    "Provided token isn't active", StringUtils.EMPTY);
            throw new OAuth2AuthenticationException(error);
        } else {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            var claims = authorizedToken.getClaims();
            if (!CollectionUtils.isEmpty(claims)) {
                populateAuthorities(claims, OAuth2TokenIntrospectionClaimNames.SCOPE,
                        v -> String.format(SCOPE_PREFIX, v), authorities);
                populateAuthorities(claims, OAuth2TokenIntrospectionClaimAdditionalNames.ROLES, Function.identity(),
                        authorities);
            }
            return new OAuth2IntrospectionAuthenticatedPrincipal(authorizedToken.getClaims(), authorities);
        }
    }

    private OAuth2Authorization.Token<OAuth2Token> getOAuth2Token(String token) {
        OAuth2Authorization authorization = oAuth2AuthorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
        if (authorization != null) {
            return authorization.getToken(token);
        }
        return null;
    }
}
