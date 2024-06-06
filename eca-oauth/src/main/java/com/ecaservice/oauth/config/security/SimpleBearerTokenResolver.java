package com.ecaservice.oauth.config.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;

/**
 * Simple bearer token resolver.
 *
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor
public class SimpleBearerTokenResolver implements BearerTokenResolver {

    private final BearerTokenResolver delegate;

    @Override
    public String resolve(HttpServletRequest request) {
        String token = delegate.resolve(request);
        if (StringUtils.isBlank(token)) {
            OAuth2Error error =
                    new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST, "Authentication required", StringUtils.EMPTY);
            throw new OAuth2AuthenticationException(error);
        }
        return token;
    }
}
