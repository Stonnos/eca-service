package com.ecaservice.oauth.security.authentication;

import com.ecaservice.oauth.config.AppProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import static com.ecaservice.oauth.util.CookiesUtils.ACCESS_TOKEN_COOKIE;
import static com.ecaservice.oauth.util.CookiesUtils.ALL_PATH;
import static com.ecaservice.oauth.util.CookiesUtils.REFRESH_TOKEN_COOKIE;
import static com.ecaservice.oauth.util.CookiesUtils.expiredCookie;

/**
 * Oauth2 token revocation success handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor
public class Oauth2TokenRevocationSuccessHandler implements AuthenticationSuccessHandler {

    private final AppProperties appProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) {
        if (appProperties.getSecurity().isWriteTokenInCookie()) {
            response.addCookie(expiredCookie(ACCESS_TOKEN_COOKIE, ALL_PATH));
            response.addCookie(
                    expiredCookie(REFRESH_TOKEN_COOKIE, appProperties.getSecurity().getRefreshTokenCookiePath()));
            log.info("Access/refresh token cookies has been expired after revocation");
        }
        response.setStatus(HttpStatus.OK.value());
    }
}
