package com.ecaservice.external.api.test.config.oauth2;

import com.ecaservice.external.api.test.config.ExternalApiTestsConfig;
import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

/**
 * Feign client configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
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

    /**
     * Creates form encoder bean.
     *
     * @param messageConverters - message converters
     * @return form encoder
     */
    @Bean
    public Encoder feignFormEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
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
