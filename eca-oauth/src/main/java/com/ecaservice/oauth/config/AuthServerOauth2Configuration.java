package com.ecaservice.oauth.config;

import com.ecaservice.oauth.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

import javax.inject.Inject;

/**
 * Oauth2 authorization server configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableAuthorizationServer
public class AuthServerOauth2Configuration extends AuthorizationServerConfigurerAdapter {

    @Inject
    private AuthenticationManager authenticationManager;
    @Inject
    private UserDetailsServiceImpl userDetailsService;

    /**
     * Creates oauth2 config bean.
     *
     * @return oauth2 config bean
     */
    @Bean
    public Oauth2Config oauth2Config() {
        return new Oauth2Config();
    }

    @Override
    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients)
            throws Exception {
        clients.inMemory()
                .withClient(oauth2Config().getClientId())
                .secret(String.format("{noop}%s", oauth2Config().getSecret()))
                .authorizedGrantTypes(oauth2Config().getGrantTypes())
                .scopes(oauth2Config().getScopes())
                .autoApprove(oauth2Config().getAutoApprove());
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager).userDetailsService(userDetailsService);
    }
}

