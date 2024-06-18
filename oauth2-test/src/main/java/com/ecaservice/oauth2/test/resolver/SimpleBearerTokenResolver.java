package com.ecaservice.oauth2.test.resolver;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
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
public class SimpleBearerTokenResolver implements BearerTokenResolver {

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public String resolve(HttpServletRequest request) {
        String token = getTokenFromHeader(request);
        if (StringUtils.isBlank(token)) {
            OAuth2Error error =
                    new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST, "Authentication required", StringUtils.EMPTY);
            throw new OAuth2AuthenticationException(error);
        }
        return token;
    }

    private String getTokenFromHeader(HttpServletRequest request) {
        String authenticationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotBlank(authenticationHeader)) {
            return StringUtils.substringAfter(authenticationHeader, BEARER_PREFIX);
        }
        return null;
    }
}
