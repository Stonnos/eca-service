package com.ecaservice.external.api.test.config.oauth2;

import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
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
@Configuration
public class FeignClientConfiguration {

    /**
     * Creates oauth2 request interceptor.
     *
     * @param clientCredentialsResourceDetails - client credentials resource details
     * @return request interceptor bean
     */
    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor(
            ClientCredentialsResourceDetails clientCredentialsResourceDetails) {
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
}
