package com.ecaservice.oauth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;

import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

/**
 * Cookies utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class CookiesUtils {

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
    public static Cookie expiredCookie(String cookieName, String path) {
        Cookie tokenCookie = new Cookie(cookieName, StringUtils.EMPTY);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setAttribute(SAME_SITE_ATTRIBUTE, STRICT);
        tokenCookie.setMaxAge(0);
        tokenCookie.setPath(path);
        return tokenCookie;
    }

    /**
     * Gets cookie from request.
     *
     * @param request    - http servlet request.
     * @param cookieName - cookie name
     * @return cookie value
     */
    public static String getCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null || request.getCookies().length == 0) {
            return null;
        }
        return Stream.of(request.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieName))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
