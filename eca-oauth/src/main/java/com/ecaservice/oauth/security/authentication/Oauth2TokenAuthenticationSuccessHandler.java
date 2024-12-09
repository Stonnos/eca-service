package com.ecaservice.oauth.security.authentication;

import com.ecaservice.oauth.config.AppProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static com.ecaservice.oauth.util.Oauth2Utils.ACCESS_TOKEN_COOKIE;
import static com.ecaservice.oauth.util.Oauth2Utils.ALL_PATH;
import static com.ecaservice.oauth.util.Oauth2Utils.REFRESH_TOKEN_COOKIE;
import static com.ecaservice.oauth.util.Oauth2Utils.tokenCookie;

/**
 * Oauth2 token authentication success handler.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class Oauth2TokenAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AppProperties appProperties;
    private final HttpMessageConverter<OAuth2AccessTokenResponse> accessTokenHttpResponseConverter =
            new OAuth2AccessTokenResponseHttpMessageConverter();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (appProperties.getSecurity().isWriteTokenInCookie() &&
                appProperties.getSecurity().getTokenInCookieAvailableGrantTypes().contains(grantType)) {
            // Writes token response in cookie
            sendAccessTokenResponseInCookie(response, authentication);
        } else {
            // Writes token response in json body
            sendAccessTokenResponse(response, authentication);
        }
    }

    private void sendAccessTokenResponseInCookie(HttpServletResponse response, Authentication authentication) {
        var accessTokenAuthentication = (OAuth2AccessTokenAuthenticationToken) authentication;
        Cookie accessTokenCookie =
                tokenCookie(ACCESS_TOKEN_COOKIE, accessTokenAuthentication.getAccessToken(), ALL_PATH);
        Cookie refreshTokenCookie = tokenCookie(REFRESH_TOKEN_COOKIE, accessTokenAuthentication.getRefreshToken(),
                appProperties.getSecurity().getRefreshTokenCookiePath());
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        response.setStatus(HttpStatus.OK.value());
    }

    private void sendAccessTokenResponse(HttpServletResponse response,
                                         Authentication authentication) throws IOException {

        OAuth2AccessTokenAuthenticationToken accessTokenAuthentication =
                (OAuth2AccessTokenAuthenticationToken) authentication;

        OAuth2AccessToken accessToken = accessTokenAuthentication.getAccessToken();
        OAuth2RefreshToken refreshToken = accessTokenAuthentication.getRefreshToken();
        Map<String, Object> additionalParameters = accessTokenAuthentication.getAdditionalParameters();

        OAuth2AccessTokenResponse.Builder builder =
                OAuth2AccessTokenResponse.withToken(accessToken.getTokenValue())
                        .tokenType(accessToken.getTokenType())
                        .scopes(accessToken.getScopes());
        if (accessToken.getIssuedAt() != null && accessToken.getExpiresAt() != null) {
            builder.expiresIn(ChronoUnit.SECONDS.between(accessToken.getIssuedAt(), accessToken.getExpiresAt()));
        }
        if (refreshToken != null) {
            builder.refreshToken(refreshToken.getTokenValue());
        }
        if (!CollectionUtils.isEmpty(additionalParameters)) {
            builder.additionalParameters(additionalParameters);
        }
        OAuth2AccessTokenResponse accessTokenResponse = builder.build();
        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        this.accessTokenHttpResponseConverter.write(accessTokenResponse, null, httpResponse);
    }
}
