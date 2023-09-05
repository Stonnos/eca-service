package com.ecaservice.feign.oauth.config;

import com.ecaservice.feign.oauth.interceptor.BearerTokenRequestInterceptor;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

/**
 * Feign configuration for oauth2 secured endpoints.
 *
 * @author Roman Batygin
 */
public class FeignClientOauth2Configuration {

    private static final String TOKEN_URL_FORMAT = "%s/oauth/token";
    private static final String CLIENT_CREDENTIALS = "client_credentials";

    /**
     * Creates oauth2 request interceptor.
     *
     * @param feignOauthProperties - auth server config
     * @return request interceptor bean
     */
    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor(FeignOauthProperties feignOauthProperties) {
        var clientCredentialsResourceDetails = clientCredentialsResourceDetails(feignOauthProperties);
        return new BearerTokenRequestInterceptor(new DefaultOAuth2ClientContext(), clientCredentialsResourceDetails);
    }

    private ClientCredentialsResourceDetails clientCredentialsResourceDetails(FeignOauthProperties feignOauthProperties) {
        var clientCredentialsResourceDetails = new ClientCredentialsResourceDetails();
        clientCredentialsResourceDetails.setClientId(feignOauthProperties.getClientId());
        clientCredentialsResourceDetails.setClientSecret(feignOauthProperties.getClientSecret());
        clientCredentialsResourceDetails.setGrantType(CLIENT_CREDENTIALS);
        clientCredentialsResourceDetails.setAccessTokenUri(
                String.format(TOKEN_URL_FORMAT, feignOauthProperties.getTokenUrl()));
        return clientCredentialsResourceDetails;
    }
}
