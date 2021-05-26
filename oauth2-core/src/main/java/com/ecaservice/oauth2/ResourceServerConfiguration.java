package com.ecaservice.oauth2;

import com.ecaservice.oauth2.config.AuthServerConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties(AuthServerConfig.class)
@RequiredArgsConstructor
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    private static final String CHECK_TOKEN_ENDPOINT_FORMAT = "%s/oauth/check_token";
    private static final String SOCKET_URL = "/socket";

    private final AuthServerConfig authServerConfig;

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .authorizeRequests()
                .antMatchers(SOCKET_URL).authenticated()
                .anyRequest().permitAll();
    }

    @Override
    public void configure(final ResourceServerSecurityConfigurer config) {
        config.tokenServices(tokenServices());
    }

    /**
     * Creates remote token service bean.
     *
     * @return remote remote token service bean
     */
    @Primary
    @Bean
    public RemoteTokenServices tokenServices() {
        RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
        remoteTokenServices.setCheckTokenEndpointUrl(
                String.format(CHECK_TOKEN_ENDPOINT_FORMAT, authServerConfig.getBaseUrl()));
        remoteTokenServices.setClientId(authServerConfig.getClientId());
        remoteTokenServices.setClientSecret(authServerConfig.getClientSecret());
        return remoteTokenServices;
    }
}
