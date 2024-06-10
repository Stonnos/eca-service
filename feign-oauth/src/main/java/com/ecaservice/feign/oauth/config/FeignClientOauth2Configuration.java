package com.ecaservice.feign.oauth.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.cloud.openfeign.security.OAuth2AccessTokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * Feign configuration for oauth2 secured endpoints.
 *
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor
public class FeignClientOauth2Configuration {

    /**
     * Creates oauth2 authorized client manager bean.
     *
     * @return oauth2 authorized client manager
     */
    @Bean
    public OAuth2AuthorizedClientManager feignOauthAuthorizedClientManager(
            OAuth2ClientProperties oAuth2ClientProperties) {
        var clientRegistrations = oAuth2ClientProperties.getRegistration().entrySet()
                .stream()
                .map(entry -> clientRegistration(oAuth2ClientProperties, entry.getKey(), entry.getValue()))
                .toList();
        log.info("Feign client registrations has been configured");
        var clientRegistrationRepository = new InMemoryClientRegistrationRepository(clientRegistrations);
        var oAuth2AuthorizedClientService = new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
        return new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository,
                oAuth2AuthorizedClientService);

    }

    /**
     * Creates oauth2 access token interceptor.
     *
     * @param feignOauthAuthorizedClientManager - oauth2 authorized client manager
     * @return oauth2 access token interceptor
     */
    @Bean
    @ConditionalOnBean(OAuth2AuthorizedClientManager.class)
    public OAuth2AccessTokenInterceptor oAuth2AccessTokenInterceptor(
            OAuth2ClientProperties oAuth2ClientProperties,
            OAuth2AuthorizedClientManager feignOauthAuthorizedClientManager) {
        String clientRegistrationId = oAuth2ClientProperties.getRegistration().keySet().iterator().next();
        Assert.notNull(clientRegistrationId, "Client registration must be specified for oauth2 client!");
        return new OAuth2AccessTokenInterceptor(clientRegistrationId, feignOauthAuthorizedClientManager);
    }

    private ClientRegistration clientRegistration(OAuth2ClientProperties oAuth2ClientProperties,
                                                  String id,
                                                  OAuth2ClientProperties.Registration registration) {
        OAuth2ClientProperties.Provider provider =
                oAuth2ClientProperties.getProvider().get(registration.getProvider());
        String tokenUri = Optional.ofNullable(provider)
                .map(OAuth2ClientProperties.Provider::getTokenUri)
                .orElse(null);
        return ClientRegistration.withRegistrationId(id)
                .clientId(registration.getClientId())
                .clientSecret(registration.getClientSecret())
                .authorizationGrantType(
                        new AuthorizationGrantType(registration.getAuthorizationGrantType()))
                .scope(registration.getScope())
                .tokenUri(tokenUri)
                .build();
    }
}
