package com.ecaservice.oauth.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Oauth2 token customizer.
 *
 * @author Roman Batygin
 */
@Slf4j
public class OAuth2TokenCustomizerImpl implements OAuth2TokenCustomizer<OAuth2TokenClaimsContext> {
    @Override
    public void customize(OAuth2TokenClaimsContext context) {
        if (context.getPrincipal() instanceof UsernamePasswordAuthenticationToken) {
            List<String> roles = context.getPrincipal().getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            log.info("Roles [{}] has been set to oauth2 token context with grant type [{}] for user [{}]", roles,
                    context.getAuthorizationGrantType().getValue(), context.getPrincipal().getName());
            context.getClaims().claim(OAuth2TokenIntrospectionClaimAdditionalNames.ROLES, roles);
        }
    }
}
