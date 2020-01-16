package com.ecaservice.web.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration for passing URL to Angular router.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableConfigurationProperties(WebConfig.class)
public class WebMvcConfiguration implements WebMvcConfigurer {

    private static final String FORWARD = "forward:/";
    private static final String LOGIN_URL = "/login";
    private static final String DASHBOARD_URLS = "/dashboard/**";

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController(LOGIN_URL).setViewName(FORWARD);
        registry.addViewController(DASHBOARD_URLS).setViewName(FORWARD);
    }
}
