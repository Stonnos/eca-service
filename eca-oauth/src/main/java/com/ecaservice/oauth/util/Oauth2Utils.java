package com.ecaservice.oauth.util;

import jakarta.servlet.http.Cookie;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/**
 * Oauth2 utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Oauth2Utils {

    /**
     * Tfa code grant type
     */
    public static final AuthorizationGrantType TFA_CODE = new AuthorizationGrantType("tfa_code");

    /**
     * Access token cookie
     */
    public static final String ACCESS_TOKEN_COOKIE = "AccessToken";

    /**
     * Refresh token cookie
     */
    public static final String REFRESH_TOKEN_COOKIE = "RefreshToken";


    /**
     * All path
     */
    public static final String ALL_PATH = "/";

    private static final String SAME_SITE_ATTRIBUTE = "SameSite";
    private static final String STRICT = "Strict";

    /**
     * Populates authorities from claims property.
     *
     * @param claims                   - claims
     * @param attributeName            - claims attribute name
     * @param authorityMappingFunction - authority mapping function
     * @param authorities              - authorities list
     */
    public static void populateAuthorities(Map<String, Object> claims,
                                           String attributeName,
                                           Function<String, String> authorityMappingFunction,
                                           Collection<GrantedAuthority> authorities) {
        Object authoritiesValue = claims.get(attributeName);
        if (authoritiesValue != null) {
            Collection<String> authoritiesList = (Collection<String>) authoritiesValue;
            for (String authorityValue : authoritiesList) {
                authorities.add(new SimpleGrantedAuthority(authorityMappingFunction.apply(authorityValue)));
            }
        }
    }

    /**
     * Gets oauth2 client authentication token.
     *
     * @param authentication - authentication object
     * @return oauth2 client authentication token
     */
    public static OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(
            Authentication authentication) {
        OAuth2ClientAuthenticationToken clientPrincipal = null;
        if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
            clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
        }
        if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
            return clientPrincipal;
        }
        throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
    }

    /**
     * Creates token cookie with attributes: httpOnly=true, SameSite=Strict.
     *
     * @param cookieName - token cookie name
     * @param token      - token
     * @param path       - cookie path
     * @return token cookie
     */
    public static Cookie tokenCookie(String cookieName, AbstractOAuth2Token token, String path) {
        Cookie tokenCookie = new Cookie(cookieName, token.getTokenValue());
        tokenCookie.setHttpOnly(true);
        tokenCookie.setAttribute(SAME_SITE_ATTRIBUTE, STRICT);
        int maxAge = (int) ChronoUnit.SECONDS.between(token.getIssuedAt(), token.getExpiresAt());
        tokenCookie.setMaxAge(maxAge);
        tokenCookie.setPath(path);
        return tokenCookie;
    }

    /**
     * Erases token cookie.
     *
     * @param cookieName - token cookie name
     * @param path       - cookie path
     * @return erased token cookie
     */
    public static Cookie eraseCookie(String cookieName, String path) {
        Cookie tokenCookie = new Cookie(cookieName, StringUtils.EMPTY);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setAttribute(SAME_SITE_ATTRIBUTE, STRICT);
        tokenCookie.setMaxAge(0);
        tokenCookie.setPath(path);
        return tokenCookie;
    }
}
