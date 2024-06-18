package com.ecaservice.oauth2.web;

import com.ecaservice.oauth2.util.OAuth2TokenIntrospectionClaimAdditionalNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import static com.ecaservice.oauth2.util.Oauth2Utils.populateAuthorities;

/**
 * Custom token introspector.
 *
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor
public class CustomTokenIntrospector implements OpaqueTokenIntrospector {

    private final OpaqueTokenIntrospector delegate;

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = delegate.introspect(token);
        var claims = oAuth2AuthenticatedPrincipal.getAttributes();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        // Populates roles from claims
        populateAuthorities(claims, OAuth2TokenIntrospectionClaimAdditionalNames.ROLES, Function.identity(),
                authorities);
        authorities.addAll(oAuth2AuthenticatedPrincipal.getAuthorities());
        log.info("Token authorities: {}", authorities);
        return new DefaultOAuth2AuthenticatedPrincipal(claims, authorities);
    }
}
