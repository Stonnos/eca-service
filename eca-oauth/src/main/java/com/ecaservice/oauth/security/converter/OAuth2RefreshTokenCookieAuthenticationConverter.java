package com.ecaservice.oauth.security.converter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2RefreshTokenAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.StringUtils;

import java.util.Collections;

import static com.ecaservice.oauth.util.CookiesUtils.REFRESH_TOKEN_COOKIE;
import static com.ecaservice.oauth.util.CookiesUtils.getCookie;

/**
 * Oauth2 refresh token cookie authentication converter.
 *
 * @author Roman Batygin
 */
@Slf4j
public final class OAuth2RefreshTokenCookieAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        log.info("Starting to get refresh token from cookie");
        // grant_type (REQUIRED)
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(grantType)) {
            return null;
        }
        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        // refresh_token (REQUIRED)
        String refreshToken = getCookie(request, REFRESH_TOKEN_COOKIE);
        if (!StringUtils.hasText(refreshToken)) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST,
                    "Refresh token cookie must be supplied", null);
            throw new OAuth2AuthenticationException(error);
        }
        log.info("Refresh token has been fetched from cookie for refreshing");
        return new OAuth2RefreshTokenAuthenticationToken(refreshToken, clientPrincipal, Collections.emptySet(),
                Collections.emptyMap());
    }
}
