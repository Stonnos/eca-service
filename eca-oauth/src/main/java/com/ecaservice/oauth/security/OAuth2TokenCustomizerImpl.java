package com.ecaservice.oauth.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.util.Assert;

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
        Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, context.getPrincipal(),
                String.format("Principal must be instance of [%s]",
                        UsernamePasswordAuthenticationToken.class.getSimpleName()));
        List<String> roles = context.getPrincipal().getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        log.info("Roles [{}] has been set to oauth2 token context for user [{}]", roles,
                context.getPrincipal().getName());
        context.getClaims().claim(OAuth2TokenIntrospectionClaimAdditionalNames.ROLES, roles);
    }
}
