package com.ecaservice.oauth2.test.introspect;

import com.ecaservice.oauth2.test.configuration.Oauth2TestConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static com.ecaservice.oauth2.test.util.TokenUtils.ACCESS_TOKEN;
import static com.ecaservice.user.model.Role.ROLE_SUPER_ADMIN;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Token introspector mock service.
 *
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor
public class TokenIntrospectorMockService implements OpaqueTokenIntrospector {
    private static final String SCOPE_PREFIX = "SCOPE_%s";

    private final Oauth2TestConfig oauth2TestConfig;

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        if (!ACCESS_TOKEN.equals(token)) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN,
                    "Provided token isn't active", StringUtils.EMPTY);
            throw new OAuth2AuthenticationException(error);
        } else {
            Map<String, Object> claims = newHashMap();
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            claims.put(OAuth2TokenIntrospectionClaimNames.SCOPE, Collections.singletonList(oauth2TestConfig.getScope()));
            authorities.add(new SimpleGrantedAuthority(String.format(SCOPE_PREFIX, oauth2TestConfig.getScope())));
            authorities.add(new SimpleGrantedAuthority(ROLE_SUPER_ADMIN));
            return new OAuth2IntrospectionAuthenticatedPrincipal(claims, authorities);
        }
    }
}
