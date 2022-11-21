package com.ecaservice.zuul.config;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Zuul gate configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableGlobalExceptionHandler
public class ZuulGateConfiguration {

    private static final String ALL = "*";
    private static final String PATH = "/**";

    /**
     * Creates cross origin filter bean.
     *
     * @return cross origin filter bean
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Collections.singletonList(ALL));
        config.setAllowedHeaders(Collections.singletonList(ALL));
        config.setAllowedMethods(Arrays.stream(HttpMethod.values()).map(HttpMethod::name).collect(Collectors.toList()));
        source.registerCorsConfiguration(PATH, config);
        return new CorsFilter(source);
    }
}
