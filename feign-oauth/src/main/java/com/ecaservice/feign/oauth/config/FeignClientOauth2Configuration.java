package com.ecaservice.feign.oauth.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.cloud.openfeign.security.OAuth2AccessTokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.util.Assert;

/**
 * Feign configuration for oauth2 secured endpoints.
 *
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor
public class FeignClientOauth2Configuration {

    private final OAuth2ClientProperties oAuth2ClientProperties;

    /**
     * Creates oauth2 access token interceptor.
     *
     * @param oAuth2AuthorizedClientManager - oauth2 authorized client manager
     * @return oauth2 access token interceptor
     */
    @Bean
    @ConditionalOnBean(OAuth2AuthorizedClientManager.class)
    public OAuth2AccessTokenInterceptor oAuth2AccessTokenInterceptor(
            OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager) {
        String clientRegistrationId = oAuth2ClientProperties.getRegistration().keySet().iterator().next();
        Assert.notNull(clientRegistrationId, "Client registration must be specified for oauth2 client!");
        return new OAuth2AccessTokenInterceptor(clientRegistrationId, oAuth2AuthorizedClientManager);
    }
}
