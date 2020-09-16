package com.ecaservice.oauth.config;

import com.ecaservice.oauth.config.granter.TfaCodeTokenGranter;
import com.ecaservice.oauth.config.granter.TfaResourceOwnerPasswordTokenGranter;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.oauth.service.UserDetailsServiceImpl;
import com.ecaservice.oauth.service.mail.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Oauth2 authorization server configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableAuthorizationServer
@RequiredArgsConstructor
public class AuthServerOauth2Configuration extends AuthorizationServerConfigurerAdapter {

    private final TfaConfig tfaConfig;
    private final UserEntityRepository userEntityRepository;
    private final NotificationService notificationService;
    private final AuthenticationManager authenticationManager;
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
        endpoints.tokenStore(tokenStore()).tokenGranter(tokenGranter(endpoints)).userDetailsService(userDetailsService);
    }

    /**
     * Creates authorization code services bean.
     *
     * @return authorization code services bean
     */
    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new InMemoryAuthorizationCodeServices();
    }

    /**
     * Creates token store bean.
     *
     * @return token store bean
     */
    @Bean
    public TokenStore tokenStore() {
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

    private TokenGranter tokenGranter(AuthorizationServerEndpointsConfigurer endpoints) {
        List<TokenGranter> tokenGranters = newArrayList();
        tokenGranters.add(endpoints.getTokenGranter());
        tokenGranters.add(new TfaResourceOwnerPasswordTokenGranter(authenticationManager, endpoints, tfaConfig,
                userEntityRepository, notificationService, authorizationCodeServices()));
        tokenGranters.add(new TfaCodeTokenGranter(endpoints, authorizationCodeServices()));
        return new CompositeTokenGranter(tokenGranters);
    }
}

