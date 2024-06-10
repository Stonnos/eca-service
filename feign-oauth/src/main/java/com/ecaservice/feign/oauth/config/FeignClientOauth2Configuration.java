package com.ecaservice.feign.oauth.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

/**
 * Feign configuration for oauth2 secured endpoints.
 *
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor
public class FeignClientOauth2Configuration {

    @Value("${spring.cloud.openfeign.oauth2.clientRegistrationId:}")
    private String clientRegistrationId;

    /**
     * Creates oauth2 authorized client manager bean.
     *
     * @return oauth2 authorized client manager
     */
    @Bean
    public OAuth2AuthorizedClientManager feignOauthAuthorizedClientManager(
            OAuth2ClientProperties oAuth2ClientProperties) {
        var registration = oAuth2ClientProperties.getRegistration().get(clientRegistrationId);
        Assert.notNull(registration,
                String.format("Client [%s] registration must be configured", clientRegistrationId));
        var clientRegistration = clientRegistration(oAuth2ClientProperties, registration);
        log.info("Feign client [{}] registration has been configured", clientRegistrationId);
        var clientRegistrationRepository = new InMemoryClientRegistrationRepository(clientRegistration);
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
        return new OAuth2AccessTokenInterceptor(clientRegistrationId, feignOauthAuthorizedClientManager);
    }

    private ClientRegistration clientRegistration(OAuth2ClientProperties oAuth2ClientProperties,
                                                  OAuth2ClientProperties.Registration registration) {
        String clientId = registration.getClientId();
        Assert.notNull(clientId,
                String.format("Client [%s] id must be configured", clientRegistrationId));
        String clientSecret = registration.getClientSecret();
        Assert.notNull(clientSecret,
                String.format("Client [%s] secret must be configured", clientRegistrationId));
        String grantType = registration.getAuthorizationGrantType();
        Assert.notNull(grantType,
                String.format("Client [%s] grant type must be configured", clientRegistrationId));
        Assert.notNull(registration.getProvider(),
                String.format("Client [%s] registration provider must be configured", clientRegistrationId));
        OAuth2ClientProperties.Provider provider = oAuth2ClientProperties.getProvider().get(registration.getProvider());
        Assert.notNull(provider,
                String.format("Client [%s] provider must be configured", clientRegistrationId));
        String tokenUri = provider.getTokenUri();
        Assert.notNull(tokenUri,
                String.format("Client [%s] provider tokenUri must be configured", clientRegistrationId));
        return ClientRegistration.withRegistrationId(clientRegistrationId)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .authorizationGrantType(new AuthorizationGrantType(grantType))
                .scope(registration.getScope())
                .tokenUri(tokenUri)
                .build();
    }
}
