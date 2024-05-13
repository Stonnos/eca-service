package com.ecaservice.oauth2.test.configuration;

import com.ecaservice.oauth2.test.introspect.TokenIntrospectorMockService;
import com.ecaservice.oauth2.test.resolver.SimpleBearerTokenResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Resource server configuration.
 *
 * @author Roman Batygin
 */
@TestConfiguration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class Oauth2ResourceServerConfiguration {

    private final Oauth2TestConfig oauth2TestConfig;

    /**
     * Creates bearer token resolver.
     *
     * @return bearer token resolver
     */
    @Bean
    @Primary
    public BearerTokenResolver bearerTokenResolver() {
        return new SimpleBearerTokenResolver();
    }

    /**
     * Creates resource server security filter chain.
     *
     * @param http - http security
     * @return security filter chain
     * @throws Exception in case of error
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize ->
                        authorize.anyRequest().permitAll()
                )
                .oauth2ResourceServer(c ->
                        c.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                                .opaqueToken(opaqueTokenConfigurer -> opaqueTokenConfigurer
                                        .introspector(new TokenIntrospectorMockService(oauth2TestConfig))));
        return http.build();
    }
}
