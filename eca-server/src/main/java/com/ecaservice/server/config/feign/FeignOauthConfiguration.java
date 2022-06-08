package com.ecaservice.server.config.feign;

import com.ecaservice.oauth2.config.AuthServerConfig;
import feign.RequestInterceptor;
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

/**
 * Feign configuration for oauth2 secured endpoints.
 *
 * @author Roman Batygin
 */
public class FeignOauthConfiguration {

    private static final String TOKEN_URL_FORMAT = "%s/oauth/token";
    private static final String CLIENT_CREDENTIALS = "client_credentials";

    /**
     * Creates oauth2 request interceptor.
     *
     * @param authServerConfig - auth server config
     * @return request interceptor bean
     */
    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor(AuthServerConfig authServerConfig) {
        var clientCredentialsResourceDetails = clientCredentialsResourceDetails(authServerConfig);
        return new OAuth2FeignRequestInterceptor(new DefaultOAuth2ClientContext(), clientCredentialsResourceDetails);
    }

    private ClientCredentialsResourceDetails clientCredentialsResourceDetails(AuthServerConfig authServerConfig) {
        var clientCredentialsResourceDetails = new ClientCredentialsResourceDetails();
        clientCredentialsResourceDetails.setClientId(authServerConfig.getClientId());
        clientCredentialsResourceDetails.setClientSecret(authServerConfig.getClientSecret());
        clientCredentialsResourceDetails.setGrantType(CLIENT_CREDENTIALS);
        clientCredentialsResourceDetails.setAccessTokenUri(
                String.format(TOKEN_URL_FORMAT, authServerConfig.getBaseUrl()));
        return clientCredentialsResourceDetails;
    }
}
