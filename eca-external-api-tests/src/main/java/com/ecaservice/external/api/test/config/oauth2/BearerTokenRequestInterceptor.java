package com.ecaservice.external.api.test.config.oauth2;

import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

/**
 * Bearer token request interceptor.
 *
 * @author Roman Batygin
 */
public class BearerTokenRequestInterceptor extends OAuth2FeignRequestInterceptor {

    /**
     * Constructor with parameters.
     *
     * @param oAuth2ClientContext - oauth2 client context
     * @param resource            - oauth2 resource details
     */
    public BearerTokenRequestInterceptor(OAuth2ClientContext oAuth2ClientContext,
                                         OAuth2ProtectedResourceDetails resource) {
        super(oAuth2ClientContext, resource);
    }

    @Override
    public synchronized OAuth2AccessToken getToken() {
        return super.getToken();
    }
}
