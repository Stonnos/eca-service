package com.ecaservice.oauth.config.security;

import com.ecaservice.oauth.config.AppProperties;
import com.ecaservice.oauth.security.BearerTokenAuthenticationEntryPoint;
import com.ecaservice.oauth.security.JdbcTokenIntrospector;
import com.ecaservice.oauth.security.SimpleBearerTokenResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Resource server security configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ResourceServerSecurityConfiguration {

    private static final int PUBLIC_ENDPOINTS_SECURITY_FILTER_ORDER = 1;
    private static final int RESOURCE_SERVER_SECURITY_FILTER_ORDER = 2;

    private static final String[] COMMON_WHITELIST_URLS = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/actuator/**"
    };
    private final AppProperties appProperties;

    /**
     * Creates bearer token resolver bean.
     *
     * @return bearer token resolver
     */
    @Bean
    @Primary
    public BearerTokenResolver bearerTokenResolver() {
        return new SimpleBearerTokenResolver(new DefaultBearerTokenResolver());
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
        if (!CollectionUtils.isEmpty(appProperties.getSecurity().getWhitelistUrls())) {
            whitelistUrls.addAll(appProperties.getSecurity().getWhitelistUrls());
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
     * @param http                       - http security
     * @param oAuth2AuthorizationService - oauth2 authorization service
     * @return security filter chain
     * @throws Exception in case of error
     */
    @Bean
    @Order(RESOURCE_SERVER_SECURITY_FILTER_ORDER)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http,
                                                          OAuth2AuthorizationService oAuth2AuthorizationService)
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
                                        .introspector(new JdbcTokenIntrospector(oAuth2AuthorizationService))));
        return http.build();
    }
}
