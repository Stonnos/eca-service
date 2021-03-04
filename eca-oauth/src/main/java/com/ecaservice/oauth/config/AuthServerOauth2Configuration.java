package com.ecaservice.oauth.config;

import com.ecaservice.oauth.config.granter.TokenGranterConfigurer;
import com.ecaservice.oauth.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;

/**
 * Oauth2 authorization server configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableAuthorizationServer
@RequiredArgsConstructor
public class AuthServerOauth2Configuration extends AuthorizationServerConfigurerAdapter {

    private final TokenGranterConfigurer tokenGranterConfigurer;
    private final UserDetailsServiceImpl userDetailsService;
    private final DataSource oauthDataSource;

    @Override
    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(oauthDataSource);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.tokenStore(tokenStore()).userDetailsService(userDetailsService);
        tokenGranterConfigurer.configure(endpoints);
    }

    /**
     * Creates token store bean.
     *
     * @return token store bean
     */
    @Bean
    public JdbcTokenStore tokenStore() {
        return new JdbcTokenStore(oauthDataSource);
    }

    /**
     * Creates token service bean.
     *
     * @return token service bean
     */
    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }
}

