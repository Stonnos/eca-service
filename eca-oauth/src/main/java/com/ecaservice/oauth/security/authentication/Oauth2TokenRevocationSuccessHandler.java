package com.ecaservice.oauth.security.authentication;

import com.ecaservice.oauth.config.AppProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import static com.ecaservice.oauth.util.Oauth2Utils.ACCESS_TOKEN_COOKIE;
import static com.ecaservice.oauth.util.Oauth2Utils.ALL_PATH;
import static com.ecaservice.oauth.util.Oauth2Utils.REFRESH_TOKEN_COOKIE;
import static com.ecaservice.oauth.util.Oauth2Utils.eraseCookie;

/**
 * Oauth2 token revocation success handler.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class Oauth2TokenRevocationSuccessHandler implements AuthenticationSuccessHandler {

    private final AppProperties appProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) {
        response.addCookie(eraseCookie(ACCESS_TOKEN_COOKIE, ALL_PATH));
        response.addCookie(eraseCookie(REFRESH_TOKEN_COOKIE, appProperties.getSecurity().getRefreshTokenCookiePath()));
        response.setStatus(HttpStatus.OK.value());
    }
}
