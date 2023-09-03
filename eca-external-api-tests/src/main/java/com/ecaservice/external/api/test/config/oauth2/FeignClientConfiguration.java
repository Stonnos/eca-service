package com.ecaservice.external.api.test.config.oauth2;

import com.ecaservice.external.api.test.config.ExternalApiTestsConfig;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

/**
 * Feign client configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
public class FeignClientConfiguration {

    /**
     * Creates oauth2 request interceptor.
     *
     * @param externalApiTestsConfig - external api confog
     * @return request interceptor bean
     */
    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor(ExternalApiTestsConfig externalApiTestsConfig) {
        var clientCredentialsResourceDetails = createClientCredentialsResourceDetails(externalApiTestsConfig.getAuth());
        return new BearerTokenRequestInterceptor(new DefaultOAuth2ClientContext(), clientCredentialsResourceDetails);
    }

    public ClientCredentialsResourceDetails createClientCredentialsResourceDetails(
            ExternalApiTestsConfig.AuthProperties authProperties) {
        var clientCredentialsResourceDetails = new ClientCredentialsResourceDetails();
        clientCredentialsResourceDetails.setAccessTokenUri(authProperties.getTokenUrl());
        clientCredentialsResourceDetails.setClientId(authProperties.getClientId());
        clientCredentialsResourceDetails.setClientSecret(authProperties.getClientSecret());
        log.info("Client credentials details has been configured");
        return clientCredentialsResourceDetails;
    }
}
