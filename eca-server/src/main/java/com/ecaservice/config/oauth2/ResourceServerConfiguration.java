package com.ecaservice.config.oauth2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;

/**
 * Resource server configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    private static final String CHECK_TOKEN_URL_FORMAT = "%s/oauth/check_token";

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and().authorizeRequests().anyRequest().permitAll();
    }

    @Override
    public void configure(final ResourceServerSecurityConfigurer config) {
        config.tokenServices(tokenServices());
    }

    /**
     * Creates oauth2 resource config bean.
     *
     * @return oauth2 resource config bean
     */
    @Bean
    public Oauth2ResourceConfig oauth2ResourceConfig() {
        return new Oauth2ResourceConfig();
    }

    /**
     * Creates remote token service.
     *
     * @return remote token service bean
     */
    @Bean
    @Primary
    public RemoteTokenServices tokenServices() {
        RemoteTokenServices tokenService = new RemoteTokenServices();
        tokenService.setClientId(oauth2ResourceConfig().getClientId());
        tokenService.setClientSecret(oauth2ResourceConfig().getSecret());
        tokenService.setCheckTokenEndpointUrl(
                String.format(CHECK_TOKEN_URL_FORMAT, oauth2ResourceConfig().getOauthUrl()));
        return tokenService;
    }

}
