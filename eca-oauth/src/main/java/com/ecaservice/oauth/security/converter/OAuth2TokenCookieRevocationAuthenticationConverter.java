package com.ecaservice.oauth.security.converter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenRevocationAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.StringUtils;

import static com.ecaservice.oauth.util.CookiesUtils.ACCESS_TOKEN_COOKIE;
import static com.ecaservice.oauth.util.CookiesUtils.getCookie;

/**
 * Token cookie revocation authentication request converter.
 *
 * @author Roman Batygin
 */
@Slf4j
public final class OAuth2TokenCookieRevocationAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        log.info("Starting to get access token from cookie for revocation");
        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        // token (REQUIRED)
        String accessToken = getCookie(request, ACCESS_TOKEN_COOKIE);
        if (!StringUtils.hasText(accessToken)) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST,
                    "Access token cookie must be supplied", null);
            throw new OAuth2AuthenticationException(error);
        }
        log.info("Access token has been fetched from cookie for revocation");
        return new OAuth2TokenRevocationAuthenticationToken(accessToken, clientPrincipal, null);
    }

}
