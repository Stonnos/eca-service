package com.ecaservice.server.config.feign;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

/**
 * Feign configuration for oauth2 secured endpoints.
 *
 * @author Roman Batygin
 */
public class FeignOauthConfiguration {

    private static final String BEARER_TOKEN_FORMAT = "Bearer %s";

    /**
     * Creates request interceptor bean.
     *
     * @return request interceptor bean
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return restTemplate -> {
            OAuth2Authentication oAuth2Authentication =
                    (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
            OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) oAuth2Authentication.getDetails();
            restTemplate.header(HttpHeaders.AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, details.getTokenValue()));
        };
    }
}
