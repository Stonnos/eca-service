package com.ecaservice.oauth2.config;

import com.ecaservice.oauth2.config.AuthServerProperties;
import com.ecaservice.oauth2.config.ResourceServerProperties;
import com.ecaservice.oauth2.web.BearerTokenAuthenticationEntryPoint;
import com.ecaservice.oauth2.web.CustomTokenIntrospector;
import com.ecaservice.oauth2.web.SimpleBearerTokenResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.SpringOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Resource server configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties({AuthServerProperties.class, ResourceServerProperties.class})
@RequiredArgsConstructor
public class ResourceServerConfiguration {

    private static final String[] COMMON_WHITELIST_URLS = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/actuator/**"
    };

    private static final String CHECK_TOKEN_ENDPOINT_FORMAT = "%s/oauth2/introspect";
    private static final int PUBLIC_ENDPOINTS_SECURITY_FILTER_ORDER = 0;
    private static final int RESOURCE_SERVER_SECURITY_FILTER_ORDER = 1;

    private final AuthServerProperties authServerProperties;
    private final ResourceServerProperties resourceServerProperties;

    /**
     * Creates bearer token resolver bean.
     *
     * @return bearer token resolver
     */
    @Bean
    @Primary
    public BearerTokenResolver bearerTokenResolver() {
        DefaultBearerTokenResolver defaultBearerTokenResolver = new DefaultBearerTokenResolver();
        defaultBearerTokenResolver.setAllowUriQueryParameter(true);
        return new SimpleBearerTokenResolver(defaultBearerTokenResolver);
    }

    /**
     * Creates whitelist urls security filter chain.
     *
     * @param http - http security
     * @return security filter chain
     * @throws Exception in case of error
     */
    @Bean
    @Order(PUBLIC_ENDPOINTS_SECURITY_FILTER_ORDER)
    public SecurityFilterChain whitelistSecurityUrlsFilterChain(HttpSecurity http) throws Exception {
        List<String> whitelistUrls = newArrayList(COMMON_WHITELIST_URLS);
        if (!CollectionUtils.isEmpty(resourceServerProperties.getWhitelistSecuredUrls())) {
            whitelistUrls.addAll(resourceServerProperties.getWhitelistSecuredUrls());
        }
        log.info("Resource server security whitelist urls: {}", whitelistUrls);
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                // Disabled bearer token check for specified urls
                .securityMatcher(whitelistUrls.toArray(new String[0]))
                .authorizeHttpRequests((authorize) -> authorize.anyRequest().permitAll());
        return http.build();
    }

    /**
     * Creates resource server security filter chain.
     *
     * @param http                    - http security
     * @return security filter chain
     * @throws Exception in case of error
     */
    @Bean
    @Order(RESOURCE_SERVER_SECURITY_FILTER_ORDER)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize ->
                        authorize.anyRequest().permitAll()
                )
                .oauth2ResourceServer(c ->
                        c.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                                .opaqueToken(opaqueTokenConfigurer -> opaqueTokenConfigurer
                                        .introspector(opaqueTokenIntrospector())));
        return http.build();
    }

    /**
     * Creates opaque token introspector bean.
     *
     * @return opaque token introspector bean
     */
    private OpaqueTokenIntrospector opaqueTokenIntrospector() {
        String introspectEndpoint = String.format(CHECK_TOKEN_ENDPOINT_FORMAT, authServerProperties.getBaseUrl());
        var delegate = new SpringOpaqueTokenIntrospector(introspectEndpoint, authServerProperties.getClientId(),
                authServerProperties.getClientSecret());
        return new CustomTokenIntrospector(delegate);
    }
}
